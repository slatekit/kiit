/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */
package slate.core.sms

import slate.common.http.common.{HttpConstants}
import slate.common.http.common.HttpMethod.POST
import slate.common.http.{HttpHelper, HttpClient, HttpRequest, HttpCredentials}
import slate.common.results.ResultSupportIn
import slate.common._
import slate.common.templates.Templates

/**
 * simple service to send sms messages using Twilio with support for templates and
 * countries
 *
 * @param key      : The twilio sid / account
 * @param password : The twilio password
 * @param phone    : The twilio phone number
 * @param templates: The templates supported ( See templates in utils for more info )
 * @param ctns     : The countries supported
 * @note           :
 *
    curl -X POST 'https://api.twilio.com/2010-04-01/Accounts/BCa1234567890d49dcffd51736e0e2e123/Messages.json' \
    --data-urlencode 'To=3475143333'  \
    --data-urlencode 'From=+17181234567'  \
    --data-urlencode 'Body=test from slate sms service' \
    -u ACb1234567890d49dcffd51736e0e2e123:xyz5a123456d78d415eaab7ab92e3bab
 */
class SmsServiceTwilio(key      :String ,
                       password :String ,
                       phone    :String ,
                       templates:Option[Templates] = None,
                       ctns:Option[List[CountryCode]] = None,
                       sender   :Option[(HttpRequest) => Result[Boolean]] = None)
  extends SmsService(templates, ctns) with ResultSupportIn {

  val _settings = new SmsSettings(key, password, phone)
  private val _baseUrl = s"https://api.twilio.com/2010-04-01/Accounts/${key}/Messages.json"


  /**
   * Initialize with api credentials
    *
    * @param apiKey
   */
  def this(apiKey: ApiCredentials, templates:Option[Templates]) = {
    this(apiKey.key, apiKey.pass, apiKey.account, templates)
  }


  /**
   * sends the sms message to the phone
    *
    * @param msg : message to send
   * @return
   */
  override def send(msg: SmsMessage): Result[Boolean] = {

      val phoneFinal = massagePhone(msg.countryCode, msg.phone)
      phoneFinal.flatMap[Boolean]( phone => {

        // Create an immutable http request.
        val req = new HttpRequest (
          url = _baseUrl,
          method = POST,
          params = Some(Seq[(String,String)](
            ("To", phone),
            ("From", _settings.account),
            ("Body", msg.msg)
          )),
          headers = None,
          credentials = Some(new HttpCredentials("Basic", _settings.key, _settings.password)),
          entity = None,
          connectTimeOut = HttpConstants.defaultConnectTimeOut,
          readTimeOut = HttpConstants.defaultReadTimeOut
        )

        // This optionally uses the IO monad supplied or actually posts ( impure )
        // This approach allows for testing this without actually sending a http request.
        sender.fold( post(req) )( s => s(req) )
      })
  }


  private def post(req:HttpRequest): Result[Boolean] = {
    val client = new HttpClient()
    HttpHelper.post(client, req)
  }


  /**
    * Format phone to ensure "+" and "iso" is present. e.g. "+{iso}{phone}"
    * @param iso
    * @param phone
    * @return
    */
  override def massagePhone(iso:String, phone:String):Result[String] =
  {
    // Remove the "+" and allow base function to ensure the country code is present
    val result = super.massagePhone(iso, Option(phone).getOrElse("").replace("+", ""))
    result.map( r => "+" + r)
  }
}
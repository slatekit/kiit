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

import slate.common.http.{HttpCredentials, HttpClient}
import slate.common.results.ResultSupportIn
import slate.common.{ApiCredentials, ApiKey, Strings, Result}

/**
 * simple service to send sms messages using Twilio
 * @param key      : The twilio sid / account
 * @param password : The twilio password
 * @param phone    : The twilio phone number
 * @note           :
 *
    curl -X POST 'https://api.twilio.com/2010-04-01/Accounts/ACb1234567890d49dcffd51736e0e2e123/Messages.json' \
    --data-urlencode 'To=3475143333'  \
    --data-urlencode 'From=+17181234567'  \
    --data-urlencode 'Body=test from slate sms service' \
    -u ACb1234567890d49dcffd51736e0e2e123:xyz5a123456d78d415eaab7ab92e3bab
 */
class SmsServiceTwilio(key:String, password:String, phone:String ) extends SmsService with ResultSupportIn {

  val _settings = new SmsSettings(key, password, phone)
  private val _baseUrl = s"https://api.twilio.com/2010-04-01/Accounts/${key}/Messages.json"


  /**
   * Initialize with api credentials
   * @param apiKey
   */
  def this(apiKey: ApiCredentials) = {
    this(apiKey.key, apiKey.pass, apiKey.account)
  }


  /**
   * sends the sms message to the phone
   * @param msg : message to send
   * @return
   */
  override def send(msg: SmsMessage): Result[Boolean] = {

    okOrFailure(
    {
      val phoneFinal = massagePhone(msg.countryCode, msg.phone)

      val params = Some(Seq[(String,String)](
                          ("To", phoneFinal),
                          ("From", _settings.account),
                          ("Body", msg.msg)
                        ))

      val http = new HttpClient()
      val creds = new HttpCredentials("Basic", _settings.key, _settings.password)
      val message = http.post(_baseUrl, params, credentials = Some(creds)).result.getOrElse("").asInstanceOf[String]
      message
    })
  }


  private def massagePhone(countryCode:String, phone:String):String =
  {
    var result = phone
    if (phone.length < 11 && Strings.isMatch("us", countryCode)) result = "1" + phone
    else if (phone.length < 11 && Strings.isMatch("in", countryCode)) result = "91" + phone

    if (!result.startsWith("+")) result = "+" + result

    result
  }
}
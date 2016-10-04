/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.core.email

import slate.common.{ApiCredentials, Result}
import slate.common.http.{HttpCredentials, HttpClient}
import slate.common.results.ResultSupportIn

class EmailServiceSendGrid(user:String, key:String, phone:String ) extends EmailService with ResultSupportIn {

  val _settings = new EmailSettings(user, key, phone)
  private val _baseUrl = s"https://api.sendgrid.com/api/mail.send.json"


  /**
   * Initialize with api credentials
   * @param apiKey
   */
  def this(apiKey: ApiCredentials) = {
    this(apiKey.key, apiKey.pass, apiKey.account)
  }


  override def send(msg: EmailMessage): Result[Boolean] = {

    okOrFailure(
    {
      val toFinal = msg.to

      // Parameters
      val bodyArg = if (msg.html) "html" else "text"
      val params = Some(Seq[(String,String)](
        ("api_user", _settings.user),
        ("api_key", _settings.key),
        ("to", toFinal),
        ("from", _settings.account),
        ("subject", msg.subject),
        (bodyArg, msg.body)
      ))

      // Credentials
      val creds = new HttpCredentials("Basic", _settings.user, _settings.key)

      // Post
      val http = new HttpClient()
      val message = http.post(_baseUrl, params, credentials = Some(creds)).result.getOrElse("")
        .asInstanceOf[String]

      // return message to be used in Result[Boolean]
      message
    })
  }
}

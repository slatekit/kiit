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

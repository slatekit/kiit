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

import slate.common.http.HttpHelper._
import slate.common.http.common.HttpConstants
import slate.common.http.common.HttpMethod.POST
import slate.common.templates.Templates
import slate.common.{IO, ApiCredentials, Result}
import slate.common.http.{HttpRequest, HttpCredentials, HttpClient}
import slate.common.results.ResultSupportIn

class EmailServiceSendGrid(user      : String,
                           key       : String,
                           phone     : String,
                           templates : Option[Templates] = None,
                           sender    : Option[(HttpRequest) => IO[Result[Boolean]]] = None )
  extends EmailService(templates) with ResultSupportIn {

  val _settings = new EmailSettings(user, key, phone)
  private val _baseUrl = s"https://api.sendgrid.com/api/mail.send.json"


  /**
   * Initialize with api credentials
   * @param apiKey
   */
  def this(apiKey: ApiCredentials, templates:Option[Templates]) = {
    this(apiKey.key, apiKey.pass, apiKey.account, templates)
  }


  override def send(msg: EmailMessage): IO[Result[Boolean]] = {

    // Parameters
    val bodyArg = if (msg.html) "html" else "text"

    // Create an immutable http request.
    val req = new HttpRequest (
      url = _baseUrl,
      method = POST,
      params = Some(Seq[(String,String)](
        ("api_user", _settings.user),
        ("api_key", _settings.key),
        ("to", msg.to),
        ("from", _settings.account),
        ("subject", msg.subject),
        (bodyArg, msg.body)
      )),
      headers = None,
      credentials = Some(new HttpCredentials("Basic", _settings.user, _settings.key)),
      entity = None,
      connectTimeOut = HttpConstants.defaultConnectTimeOut,
      readTimeOut = HttpConstants.defaultReadTimeOut
    )

    // This optionally uses the IO monad supplied or actually posts ( impure )
    // This approach allows for testing this without actually sending a http request.
    sender.fold( post(req) )( s => s(req) )
  }


  private def post(req:HttpRequest): IO[Result[Boolean]] = {
    val client = new HttpClient()
    postIO(client, req)
  }
}

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

package slate.core.mobile

import slate.common.Result
import slate.common.http.{HttpCredentials, HttpClient}
import slate.common.results.ResultSupportIn

class MessageServiceGoogle(private val _key:String) extends MessageService with ResultSupportIn {

  val _settings = new MessageSettings("", _key, "")
  val _baseUrl = "https://android.googleapis.com/gcm/send"


  override def send(msg: Message): Result[Boolean] = {

    okOrFailure({
      val payload = msg.toJsonCompact()
      val content = "{ \"to\" : \"" + msg.toDevice + "\", \"data\" : " + payload + " }"

      // Headers.
      val headers = Some(Seq[(String,String)](
        ("Content-Type", "application/json"),
        ("Authorization", "key=" + _settings.key)
      ))

      val http = new HttpClient()
      val message = http.post(_baseUrl, entity = Some(content), headers = headers).result.getOrElse("").asInstanceOf[String]
      message
    })
  }
}
/*
byte[] bytes = body.getBytes(UTF8);
HttpURLConnection conn = getConnection(url);
conn.setDoOutput(true);
conn.setUseCaches(false);
conn.setFixedLengthStreamingMode(bytes.length);
conn.setRequestMethod("POST");
conn.setRequestProperty("Content-Type", "application/json");
conn.setRequestProperty("Authorization", "key=" + key);
OutputStream out = conn.getOutputStream();
*/
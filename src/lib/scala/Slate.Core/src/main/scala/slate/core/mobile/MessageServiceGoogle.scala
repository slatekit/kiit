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

package slate.core.mobile

import slate.common.Result
import slate.common.http.{HttpCredentials, HttpClient}
import slate.common.results.ResultSupportIn

class MessageServiceGoogle(private val _key:String) extends MessageService with ResultSupportIn {

  var _settings = new MessageSettings("", _key, "")
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
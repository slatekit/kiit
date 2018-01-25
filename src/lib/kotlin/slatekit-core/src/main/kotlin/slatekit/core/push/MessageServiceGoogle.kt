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

package slatekit.core.push

import slatekit.common.Result
import slatekit.common.http.*
import slatekit.common.results.ResultFuncs


class MessageServiceGoogle(private val _key:String) : MessageServiceBase() {

  val _settings = MessageSettings("", _key, "")
  val _baseUrl2 = "https://android.googleapis.com/gcm/send"
  val _baseUrl = "https://gcm-http.googleapis.com/gcm/send"


  override fun send(msg: Message): Result<Boolean> {

      val payload = msg.toJsonCompact()
      val content = "{ \"to\" : \"" + msg.toDevice + "\", \"data\" : " + payload + " }"

      // Create an immutable http request.
      val req = HttpRequest(
              url = _baseUrl,
              method = HttpMethod.POST,
              params = null,
              headers = listOf(
                  Pair("Content-Type", "application/json"),
                  Pair("Authorization", "key=" + _settings.key)
              ),
              credentials = null,
              entity = content,
              connectTimeOut = HttpConstants.defaultConnectTimeOut,
              readTimeOut = HttpConstants.defaultReadTimeOut
      )
      val res = HttpClient.post(req)
      val result = if (res.is2xx) ResultFuncs.success(true, msg = res.result?.toString() ?: "")
      else ResultFuncs.err("error sending sms to ${req.url}")
      return result
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
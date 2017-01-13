package slate.common.http

/**
 * Created by kv on 10/23/2015.
 */
object HttpUtility {

  /**
   * Gets the content of web url.
  * @example get("http://www.example.com/getInfo")
  * @example get("http://www.example.com/getInfo", 5000)
  * @example get("http://www.example.com/getInfo", 5000, 5000)
  */
  @throws(classOf[java.io.IOException])
  @throws(classOf[java.net.SocketTimeoutException])
  def get(url: String, connectTimeout:Int =5000, readTimeout:Int =5000, requestMethod: String = "GET"):String = {
    import java.net.{HttpURLConnection, URL}

    // connection
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]

    // time outs
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)

    // get
    connection.setRequestMethod(requestMethod)

    // Get content
    val inputStream = connection.getInputStream
    val content = io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close
    content
  }
}

/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.common.http

import java.io.{InputStream, InputStreamReader, BufferedReader}
import java.net.{URLEncoder, HttpURLConnection, URL}
import java.nio.charset.StandardCharsets

import slate.common.{Strings, IO, Result}
import slate.common.results.{ResultSupportIn, ResultCode}

//import com.sun.deploy.net.URLEncoder
import com.sun.org.apache.xml.internal.security.utils.Base64


object HttpHelper extends ResultSupportIn {

  def postIO(client: HttpClient, req:HttpRequest): IO[Result[Boolean]] = {
    new IO[Result[Boolean]]( () => post(client, req) )
  }


  def post(client: HttpClient, req:HttpRequest):Result[Boolean] = {
    var res = (false, ResultCode.SUCCESS, "")
    try {
      val result = client.post(req)
      res = (result.is2xx, result.statusCode, result.result.getOrElse("").toString)
    }
    catch{
      case ex:Exception => {
        res = (false, ResultCode.UNEXPECTED_ERROR, ex.getMessage)
      }
    }
    successOrErrorWithCode[Boolean](res._1, res._1, res._2, Option(res._3))
  }


  def createGetUrl(req:HttpRequest ):URL =
  {
    var url:URL = null

    // The url has to be built up
    if(req.params.isDefined)
    {
      val params = encodeParams(req)
      url = new URL( req.url + "?" + params )
    }
    else
    {
      url = new URL(req.url)
    }
    url
  }


  def setCredentials(con:HttpURLConnection, req:HttpRequest):Unit =
  {
    // Set credentials
    if(req.credentials.isDefined)
    {
      val credentials = req.credentials.get
      val userPassword = credentials.name + ":" + credentials.password
      val encoding = Base64.encode(userPassword.getBytes(StandardCharsets.UTF_8))
      val finalEncoding = encoding.replaceAll("\n", "").replaceAll("\r", "")
      con.setRequestProperty("Authorization", "Basic " + finalEncoding)
    }
  }


  def encodeParams(req: HttpRequest):Option[String] =
  {
    if(req.params.isDefined && req.params.get.size == 0) {
      val params = req.params.fold("")(params => {
        Strings.mkString[(String, String)](params, p => {
          URLEncoder.encode(p._1, "UTF-8") + "=" + URLEncoder.encode(p._2, "UTF-8")
        }, "&")
      })
      Some(params)
    }
    else
      None
  }


  def readInputStreamAsString(is:InputStream):String =
  {
    val reader = new BufferedReader(new InputStreamReader(is, "is-8859-1"), 8)
    val sb= new StringBuilder()
    var line = reader.readLine()
    while (line != null) {
      sb.append(line + "\n")
      line = reader.readLine()
    }
    is.close()
    sb.toString()
  }
}

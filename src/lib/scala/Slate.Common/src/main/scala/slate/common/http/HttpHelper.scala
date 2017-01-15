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
    val res = try {
      val result = client.post(req)
      (result.is2xx, result.statusCode, result.result.getOrElse("").toString)
    }
    catch{
      case ex:Exception => {
        (false, ResultCode.UNEXPECTED_ERROR, ex.getMessage)
      }
    }
    successOrErrorWithCode[Boolean](res._1, res._1, res._2, Option(res._3))
  }


  def createGetUrl(req:HttpRequest ):URL =
  {
    // The url has to be built up
    if(req.params.isDefined)
    {
      val params = encodeParams(req)
      new URL( req.url + "?" + params )
    }
    else
    {
      new URL(req.url)
    }
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
}

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

package slate.common.http

import java.io.{InputStream, DataOutputStream}
import java.net.{URL, HttpURLConnection}

import slate.common.http.common.{HttpStatus, HttpConstants, HttpMethod}


class HttpClient {

  /**
   * Creates an http get request
   * @param url            : Url of the request
   * @param params         : parameters ( url parameters for get, form parameters for post )
   * @param headers        : http headers
   * @param credentials    : http credentials
   * @param connectTimeout : timeout of operation
   * @param readTimeout    : timeout for read operation
   */
  def get(
            url           : String,
            params        : Option[Seq[(String,String)]] = None,
            headers       : Option[Seq[(String,String)]] = None,
            credentials   : Option[HttpCredentials] = None,
            connectTimeout: Int = HttpConstants.defaultConnectTimeOut,
            readTimeout   : Int = HttpConstants.defaultReadTimeOut
         ): HttpResponse =
  {
    get(new HttpRequest(url, HttpMethod.GET, params, headers, credentials, None, connectTimeout, readTimeout ))
  }


  /**
   * executes a get request and returns the string content only.
   * @param req
   * @return
   */
  def get(req:HttpRequest): HttpResponse =
  {
    val response = tryExecute( req, () =>
    {
      // Build the url with query string.
      val url = HttpHelper.createGetUrl(req)

      // Connection
      val con = url.openConnection.asInstanceOf[HttpURLConnection]

      // Time outs
      con.setConnectTimeout(req.connectTimeOut)
      con.setReadTimeout(req.readTimeOut)

      // Headers
      if(req.headers.isDefined){
        for(pair <- req.headers.get){
          con.setRequestProperty(pair._1, pair._2)
        }
      }
      // Request method
      con.setRequestMethod(HttpMethod.GET.stringVal)
      HttpHelper.setCredentials(con, req)

      // Return build up con
      con
    })
    response
  }


  /**
   * Creates an http post request
   * @param url            : Url of the request
   * @param params         : parameters ( url parameters for get, form parameters for post )
   * @param headers        : http headers
   * @param credentials    : http credentials
   * @param entity         : http entity / content to send
   * @param connectTimeout : timeout of operation
   * @param readTimeout    : timeout for read operation
   */
  def post(
            url           : String,
            params        : Option[Seq[(String,String)]] = None,
            headers       : Option[Seq[(String,String)]] = None,
            credentials   : Option[HttpCredentials] = None,
            entity        : Option[String] = None,
            connectTimeout: Int = HttpConstants.defaultConnectTimeOut,
            readTimeout   : Int = HttpConstants.defaultReadTimeOut
            ): HttpResponse =
  {
    post(new HttpRequest(url, HttpMethod.POST, params, headers, credentials, entity, connectTimeout, readTimeout ))
  }


  /**
   * executes a Post request and returns the string content only.
   * @param req
   * @return
   */
  def post(req:HttpRequest): HttpResponse =
  {
    val response = tryExecute(req, () =>
    {
      val url = new URL(req.url)

      // Connection
      val con = url.openConnection.asInstanceOf[HttpURLConnection]

      // Set request props
      con.setRequestMethod(HttpMethod.POST.stringVal)
      HttpHelper.setCredentials(con, req)

      // Headers
      if(req.headers.isDefined){
        for(pair <- req.headers.get){
          con.setRequestProperty(pair._1, pair._2)
        }
      }

      con.setDoOutput(true)

      // Parameters
      var entity = ""
      if(req.params.isDefined){
        entity = HttpHelper.encodeParams(req).getOrElse("")
      }
      else if(req.entity.isDefined){
        entity = req.entity.get
      }
      val writer = new DataOutputStream(con.getOutputStream)
      writer.writeBytes(entity)
      writer.flush()
      writer.close()

      // Return build up con
      con
    })
    response
  }


  private def tryExecute(req:HttpRequest, callback:() => HttpURLConnection) : HttpResponse =
  {
    var inputStream:InputStream = null
    var success = false
    var statusCode = 0
    var content = ""
    var message = ""


    try
    {
      // Let the caller build up the connection
      val con = callback()

      // Get the response code
      statusCode = con.getResponseCode()

      // Get content
      inputStream = con.getInputStream
      content = io.Source.fromInputStream(inputStream).mkString
      success = true
    }
    catch {
      case ex: Exception =>
      {
        message = s"Error getting content from ${req.url}"
        println(ex.getMessage)
        success = false
      }
    }
    finally
    {
      if (inputStream != null) inputStream.close
    }
    // TODO: Get the headers and fill http response correctly.
    val code = if(statusCode == HttpStatus.sOk.code) HttpStatus.sOk else HttpStatus.sErr
    new HttpResponse(code, Map[String,Seq[String]](), Some(content))
  }
}

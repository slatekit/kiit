/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.http

import com.sun.org.apache.xml.internal.security.utils.Base64
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.successWithCode
import slatekit.common.results.ResultFuncs.unexpectedError
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


/**
 * Temporarily here. This Http Client Wrapper around Java.net will be replaced
 * soon with a better alternative.
 */
object HttpClient {

    //fun post(req:HttpRequest): HttpResponse = HttpResponse(HttpStatusCodes.s200, mapOf(), null)
    fun post(req: HttpRequest): HttpResponse {
        val response = tryExecute(req, {

            // 1. URL
            val url = URL(req.url)
            val con = url.openConnection() as HttpURLConnection

            // 2. METHOD
            con.requestMethod = HttpMethod.POST.toString()

            // 3. CREDS
            setCredentials(con, req)

            // 4. HEADERS
            req.headers?.let { headers ->
                headers.forEach { (first, second) -> con.setRequestProperty(first, second) }
            }

            // 5. OUTPUT
            con.setDoOutput(true)

            // Parameters
            val entity = req.params?.let { params -> encodeParams(req) } ?: req.entity ?: ""

            val writer = DataOutputStream(con.outputStream)
            writer.writeBytes(entity)
            writer.flush()
            writer.close()

            // Return build up con
            con
        })
        return HttpResponse(response.code, mapOf(), response.value)
    }


    fun encodeParams(req: HttpRequest): String? {
        return req.params?.let { params ->
            val encoded = params.fold("", { acc, p ->
                acc + "&" + URLEncoder.encode(p.first, "UTF-8") + "=" + URLEncoder.encode(p.second, "UTF-8")
            })
            encoded
        } ?: ""
    }

      /**
       * executes a get request and returns the string content only.
       * @param req
       * @return
       */
      fun get(req:HttpRequest): HttpResponse
      {
          val response = tryExecute(req, {

              // 1. URL
              val url = URL(req.url)
              val con = url.openConnection() as HttpURLConnection

              // 2. METHOD
              con.requestMethod = HttpMethod.GET.toString()

              // 3. CREDS
              setCredentials(con, req)

              // 4. HEADERS
              req.headers?.let { headers ->
                  headers.forEach { (first, second) -> con.setRequestProperty(first, second) }
              }

              // 5. OUTPUT
              con.setDoOutput(true)

              val writer = DataOutputStream(con.outputStream)
              writer.flush()
              writer.close()

              // Return build up con
              con
          })
          return HttpResponse(response.code, mapOf(), response.value)
      }


    /*
      /**
       * executes a Post request and returns the string content only.
       * @param req
       * @return
       */
      fun post(req:HttpRequest): HttpResponse
      {
        val response = tryExecute(req, () ->
        {
          val url = URL(req.url)

          // Connection
          val con = url.openConnection.asInstanceOf[HttpURLConnection]

          // Set request props
          con.setRequestMethod(HttpMethod.POST.stringVal)
          HttpHelper.setCredentials(con, req)

          // Headers
          req.headers.fold(Unit)( headers -> {
            headers.foreach( h -> con.setRequestProperty(h._1, h._2))
            Unit
          })
          con.setDoOutput(true)

          // Parameters
          val entity = if(req.params.isDefined){
            HttpHelper.encodeParams(req).getOrElse("")
          }
          else
            req.entity.getOrElse("")

          val writer = DataOutputStream(con.getOutputStream)
          writer.writeBytes(entity)
          writer.flush()
          writer.close()

          // Return build up con
          con
        })
        response
      }


      private def tryExecute(req:HttpRequest, callback:() -> HttpURLConnection) : HttpResponse =
      {
        val result = try
        {
          // Let the caller build up the connection
          val con = callback()

          // Get the response code
          val statusCode = con.getResponseCode()

          // Get content
          val inputStream = con.getInputStream
          val content = io.Source.fromInputStream(inputStream).mkString
          (true, statusCode, content, Option(inputStream))
        }
        catch {
          case ex: Exception ->
          {
            val msg = s"Error getting content from ${req.url}." + ex.getMessage()
            (false, HttpStatus.s500.code, msg, None)
          }
        }

        val inputStream = result._4
        inputStream.foreach( i -> i.close())

        // TODO: Get the headers and fill http response correctly.
        val code = if(result._2 == HttpStatus.sOk.code) HttpStatus.sOk else HttpStatus.sErr
        HttpResponse(code, Map[String,Seq[String]](), Option(result._3))
      }
      */


    fun tryExecute(req: HttpRequest, callback: () -> HttpURLConnection): Result<Any> {
        val result = try {
            // Let the caller build up the connection
            val con = callback()

            // Get the response code
            val statusCode = con.responseCode

            // Get content
            val buff = BufferedReader(InputStreamReader(con.inputStream))
            var line: String? = buff.readLine()
            val response = StringBuffer()
            while (line != null) {
                response.append(line)
                line = buff.readLine()
            }
            buff.close()
            val content = response.toString()
            successWithCode(statusCode, content)
        }
        catch(ex: Exception) {
            val msg = "Error getting content from ${req.url}." + ex.message
            unexpectedError<Boolean>(msg)
        }
        return result
    }


    fun setCredentials(con: HttpURLConnection, req: HttpRequest): Unit {
        // Set credentials
        if (req.credentials != null) {
            val credentials = req.credentials
            val userPassword = credentials.name + ":" + credentials.password
            val encoding = Base64.encode(userPassword.byteInputStream(StandardCharsets.UTF_8).readBytes())
            val finalEncoding = encoding.replace("\n", "").replace("\r", "")
            con.setRequestProperty("Authorization", "Basic " + finalEncoding)
        }
    }
}

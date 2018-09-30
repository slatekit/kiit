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

package slatekit.server.ktor

import io.ktor.application.ApplicationCall
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.apis.ApiConstants
import slatekit.common.*
import slatekit.server.ServerConfig
import io.ktor.request.*
import java.io.*
import javax.servlet.MultipartConfigElement
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


class KtorRequest(val req: ApplicationRequest) : RequestSupport {


    /**
     * Access to the raw spark request
     */
    override fun raw():Any? = req


    /**
     * Access to an uploaded file
     * https://github.com/tipsy/spark-file-upload/blob/master/src/main/java/UploadExample.java
     * http://javasampleapproach.com/java/ways-to-convert-inputstream-to-string
     */
    override fun getDoc(name:String): Doc {
        return getFile( name, { stream ->

            val bis = BufferedInputStream(stream)
            val buf = ByteArrayOutputStream()
            var ris = bis.read()
            while (ris != -1) {
                buf.write(ris.toByte().toInt())
                ris = bis.read()
            }
            val text = buf.toString()
            Doc(name, text, ContentTypeHtml, text.length.toLong())
        })
    }


    /**
     * Access to an uploaded file
     * https://github.com/tipsy/spark-file-upload/blob/master/src/main/java/UploadExample.java
     * http://javasampleapproach.com/java/ways-to-convert-inputstream-to-string
     */
    override fun getFile(name:String, callback:(InputStream) -> Doc ): Doc {
//        req.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement("/temp"))
//        val doc = req.raw().getPart(name).getInputStream().use({ stream ->
//            callback(stream)
//        })
//        return doc
        return Doc("", "", ContentTypeText, 0)
    }


    /**
     * Access to an uploaded file
     * https://github.com/tipsy/spark-file-upload/blob/master/src/main/java/UploadExample.java
     * http://javasampleapproach.com/java/ways-to-convert-inputstream-to-string
     */
    override fun getFileStream(name:String, callback:(InputStream) -> Unit ): Unit {
        //req.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement("/temp"))
        //req.raw().getPart(name).getInputStream().use({ stream ->
        //    callback(stream)
        //})
    }


    companion object {

        fun build(ctx: Context, body:String, call: ApplicationCall, conf: ServerConfig): slatekit.common.Request {
            val req = call.request
            val rawUri = req.uri
            val uri = if (rawUri.startsWith(conf.prefix)) rawUri.substring(conf.prefix.length) else rawUri
            val parts = uri.split('/')
            // val headers = req.headers().map { key -> Pair(key, req.headers(key)) }.toMap()
            val method = req.httpMethod.value.toLowerCase()
            val json = loadJson(body, req)

            // e.g. api/app/users/register
            // parts  : [app, users, register]
            // area   : app
            // name   : users
            // action : register
            // verb   : get
            // opts   : headers
            // args   : params
            // tag    : guid

            // Reverting change to args.
            return slatekit.common.Request(
                    path = req.uri,
                    parts = parts,
                    source = ApiConstants.SourceWeb,
                    verb = method,
                    meta = KtorHeaders(req, ctx.enc),
                    data = KtorParams(body, req, ctx.enc),
                    raw  = KtorRequest(req),
                    tag = Random.stringGuid()
            )
        }


        /**
         * Load json from the post/put body using json-simple
         */
        fun loadJson(body:String, req: ApplicationRequest, addQueryParams:Boolean = false): JSONObject {
            val method = req.httpMethod.value.toLowerCase()
            val isMultiPart = req.isMultipart()
            val isBodyAllowed = isBodyAllowed(method)
            val json = if (isBodyAllowed && !isMultiPart && !body.isNullOrEmpty()) {
                val parser = JSONParser()
                val root = parser.parse(body)
                root as JSONObject

                // Add query params
                if(addQueryParams && !req.queryParameters.isEmpty()){
                    req.queryParameters.names().forEach { key ->
                        root.put(key, req.queryParameters.get(key))
                    }
                }
                root
            }
            else {
                JSONObject()
            }
            return json
        }


        fun isBodyAllowed(method:String):Boolean = method == "put" || method == "post" || method == "delete"
    }

}

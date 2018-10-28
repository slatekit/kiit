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

package slatekit.server.spark

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.apis.ApiConstants
import slatekit.common.*
import slatekit.common.content.ContentTypeHtml
import slatekit.common.content.Doc
import slatekit.server.ServerConfig
import spark.Request
import java.io.*
import javax.servlet.MultipartConfigElement

class SparkRequest(val req: Request) : RequestSupport {

    /**
     * Access to the raw spark request
     */
    override fun raw(): Any? = req

    /**
     * Access to an uploaded file
     * https://github.com/tipsy/spark-file-upload/blob/master/src/main/java/UploadExample.java
     * http://javasampleapproach.com/java/ways-to-convert-inputstream-to-string
     */
    override fun getDoc(name: String): Doc {
        return getFile(name, { stream ->

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
    override fun getFile(name: String, callback: (InputStream) -> Doc): Doc {
        req.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement("/temp"))
        val doc = req.raw().getPart(name).getInputStream().use({ stream ->
            callback(stream)
        })
        return doc
    }

    /**
     * Access to an uploaded file
     * https://github.com/tipsy/spark-file-upload/blob/master/src/main/java/UploadExample.java
     * http://javasampleapproach.com/java/ways-to-convert-inputstream-to-string
     */
    override fun getFileStream(name: String, callback: (InputStream) -> Unit) {
        req.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement("/temp"))
        req.raw().getPart(name).getInputStream().use({ stream ->
            callback(stream)
        })
    }

    companion object {

        @JvmStatic
        fun build(ctx: Context, req: Request, conf: ServerConfig): slatekit.common.Request {
            val rawUri = req.uri()
            val uri = if (rawUri.startsWith(conf.prefix)) rawUri.substring(conf.prefix.length) else rawUri
            val parts = uri.split('/')
            // val headers = req.headers().map { key -> Pair(key, req.headers(key)) }.toMap()
            val method = req.requestMethod().toLowerCase()
            val isBodyOk = isBodyAllowed(method)
            val json = loadJson(req)

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
                    path = req.uri(),
                    parts = parts,
                    source = ApiConstants.SourceWeb,
                    verb = req.requestMethod().toLowerCase(),
                    meta = SparkHeaaders(req, ctx.enc),
                    data = SparkParams(req, ctx.enc),
                    raw = SparkRequest(req),
                    tag = Random.stringGuid()
            )
        }

        /**
         * Load json from the post/put body using json-simple
         */
        @JvmStatic
        fun loadJson(req: Request, addQueryParams: Boolean = false): JSONObject {
            val method = req.requestMethod().toLowerCase()
            val isPosted = isBodyAllowed(method)
            val tpe: String? = req.contentType()
            val isMultiPart = tpe?.startsWith("multipart/form-data;") ?: false
            val json = if (isPosted && !isMultiPart && !req.body().isNullOrEmpty()) {
                val parser = JSONParser()
                val body = req.body()
                val root = parser.parse(req.body())
                root as JSONObject

                // Add query params
                if (addQueryParams && !req.queryParams().isEmpty()) {
                    req.queryParams().map { key ->
                        if (key != null) {
                            root.put(key, req.queryParams(key))
                        }
                    }
                }
                root
            } else {
                JSONObject()
            }
            return json
        }

        @JvmStatic
        fun isBodyAllowed(method: String): Boolean = method == "put" || method == "post" || method == "delete"
    }
}

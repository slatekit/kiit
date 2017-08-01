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
import slatekit.core.common.AppContext
import slatekit.server.ServerConfig
import spark.Request
import java.io.BufferedReader
import javax.servlet.MultipartConfigElement
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


class HttpRequest(val req: Request) : RequestSupport {


    /**
     * Access to the raw spark request
     */
    override fun raw():Any? = req


    /**
     * Access to an uploaded file
     */
    override fun getDoc(name:String): Doc {
        TODO.IMPLEMENT("spark", "file-upload having issues, need to check and refactor this")

        val location = System.getProperty("java.io.tmpdir")
        req.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement(location))
        val part = req.raw().getPart("name")
        val content = part.inputStream.use({ stream ->
            val textBuilder = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream, Charset.forName(StandardCharsets.UTF_8.name())))
            reader.use { r ->
                var c = reader.read()
                while (c != -1) {
                    textBuilder.append(c)
                    c = r.read()
                }
            }
            textBuilder.toString()
        })

        return Doc(name, content, ContentTypeHtml, content.length.toLong())
    }


    companion object {

        fun build(ctx: Context, req: Request, conf: ServerConfig): slatekit.common.Request {
            val rawUri = req.uri()
            val uri = if (rawUri.startsWith(conf.prefix)) rawUri.substring(conf.prefix.length) else rawUri
            val parts = uri.split('/')

            // e.g. api/app/users/register
            // parts  : [app, users, register]
            // area   : app
            // name   : users
            // action : register
            // verb   : get
            // opts   : headers
            // args   : params
            // tag    : guid
            return slatekit.common.Request(
                    path = req.uri(),
                    parts = parts,
                    protocol = ApiConstants.ProtocolWeb,
                    verb = req.requestMethod().toLowerCase(),
                    opts = HttpHeaders(req, ctx.enc),
                    args = HttpParams(req, ctx.enc),
                    raw = HttpRequest(req),
                    tag = Random.stringGuid()
            )
        }


        /**
         * Load json from the post/put body using json-simple
         */
        fun loadJson(req: Request): JSONObject {
            val method = req.requestMethod().toLowerCase()
            val isPosted = isBodyAllowed(method)
            val json = if (isPosted && !req.body().isNullOrEmpty()) {
                val parser = JSONParser()
                val root = parser.parse(req.body())
                root as JSONObject
            }
            else {
                JSONObject()
            }
            return json
        }


        fun isBodyAllowed(method:String):Boolean = method == "put" || method == "post" || method == "delete"
    }

}
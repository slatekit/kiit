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
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.http.content.streamProvider
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.common.*
import slatekit.server.ServerConfig
import io.ktor.request.*
//import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.async
import slatekit.common.content.ContentTypeHtml
import slatekit.common.content.ContentTypeText
import slatekit.common.content.Doc
import slatekit.common.requests.Request
import slatekit.common.requests.RequestSupport
import slatekit.common.requests.SimpleRequest
import java.io.*

class KtorRequest(val call: ApplicationCall, val req: ApplicationRequest) : RequestSupport {

    /**
     * Access to the raw spark request
     */
    override fun raw(): Any? = req

    /**
     * Access to an uploaded file
     */
    override fun getDoc(name: String): Doc {
        return getFile(name) { stream ->

            val bis = BufferedInputStream(stream)
            val buf = ByteArrayOutputStream()
            var ris = bis.read()
            while (ris != -1) {
                buf.write(ris.toByte().toInt())
                ris = bis.read()
            }
            val text = buf.toString()
            Doc(name, text, ContentTypeHtml, text.length.toLong())
        }
    }

    /**
     * Access to an uploaded file
     */
    override fun getFile(name: String, callback: (InputStream) -> Doc): Doc {
        // getFileStream(name, callback)
        return Doc("", "", ContentTypeText, 0)
    }

    suspend fun getFileAsync(name: String, callback: (InputStream) -> Doc): Doc {
        val multiPart = call.receiveMultipart()
        val part = multiPart.readAllParts().find { (it.name ?: "") == name }
        val doc = part?.let {
            val file = it as PartData.FileItem
            val doc = file.streamProvider().use(callback)
            doc
        } ?: Doc.empty
        return doc
    }

    /**
     * Access to an uploaded file
     */
    override fun getFileStream(name: String, callback: (InputStream) -> Unit) {
//        async {
//            val multiPart = call.receiveMultipart()
//            val part = multiPart.readAllParts().find { (it.name ?: "") == name }
//            part?.let {
//                val file = it as PartData.FileItem
//                file.streamProvider().use(callback)
//            }
//        }
    }

    companion object {

        fun build(ctx: Context, body: String, call: ApplicationCall, conf: ServerConfig): Request {
            val req = call.request
            val httpUri = req.uri
            val rawUri = if (httpUri.startsWith(conf.prefix)) httpUri.substring(conf.prefix.length) else httpUri

            // app/users/recent?count=20
            // Only get up until "?"
            val uri = if (rawUri.contains("?")) {
                rawUri.substring(0, rawUri.indexOf("?"))
            } else {
                rawUri
            }
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
            return SimpleRequest(
                    path = uri,
                    parts = parts,
                    source = slatekit.common.requests.Source.Web,
                    verb = method,
                    meta = KtorHeaders(req, ctx.enc),
                    data = KtorParams(body, req, ctx.enc),
                    raw = KtorRequest(call, req),
                    tag = Random.uuid()
            )
        }

        /**
         * Load json from the post/put body using json-simple
         */
        fun loadJson(body: String, req: ApplicationRequest, addQueryParams: Boolean = false): JSONObject {
            val method = req.httpMethod.value.toLowerCase()
            val isMultiPart = req.isMultipart()
            val isBodyAllowed = isBodyAllowed(method)
            val json = if (isBodyAllowed && !isMultiPart && !body.isNullOrEmpty()) {
                val parser = JSONParser()
                val root = parser.parse(body)
                root as JSONObject

                // Add query params
                if (addQueryParams && !req.queryParameters.isEmpty()) {
                    req.queryParameters.names().forEach { key ->
                        root.put(key, req.queryParameters.get(key))
                    }
                }
                root
            } else {
                JSONObject()
            }
            return json
        }

        fun isBodyAllowed(method: String): Boolean = method == "put" || method == "post" || method == "delete"
    }
}

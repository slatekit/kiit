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
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import slatekit.common.*
import slatekit.server.ServerSettings
import io.ktor.request.*
import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.experimental.async
import slatekit.common.types.ContentTypeHtml
import slatekit.common.types.Doc
import slatekit.common.requests.Request
import slatekit.common.requests.RequestSupport
import slatekit.common.Source
import slatekit.common.utils.Random
import java.io.*

/**
 * Represents an abstraction of a Web Api Request and also a CLI ( Command Line ) request
 * @param path : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * @param parts : list of the parts of the action e.g. [ "app", "reg", "activateUser" ]
 * @param source : protocol e.g. "cli" for command line and "http"
 * @param verb : get / post ( similar to http verb )
 * @param meta : options representing settings/configurations ( similar to http-headers )
 * @param data : arguments to the command
 * @param raw : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * @param output : Optional output format of the result e.g. json by default json | csv | props
 * @param tag : Optional tag for tracking individual requests and for error logging.
 */
data class KtorRequest(
        private  val call: ApplicationCall,
        override val path: String,
        override val parts: List<String>,
        override val source: Source,
        override val verb: String,
        override val data: Inputs,
        override val meta: Metadata,
        override val raw: Any? = null,
        override val output: String? = "",
        override val tag: String = "",
        override val version: String = "1.0",
        override val timestamp: DateTime = DateTime.now()
) : Request, RequestSupport {

    /**
     * To transform / rewrite the request
     */
    override fun clone(
            otherPath: String,
            otherParts: List<String>,
            otherSource: Source,
            otherVerb: String,
            otherData: Inputs,
            otherMeta: slatekit.common.Metadata,
            otherRaw: Any?,
            otherOutput: String?,
            otherTag: String,
            otherVersion: String,
            otherTimestamp:DateTime) : Request {
        return this.copy(
                path      = otherPath,
                parts     = otherParts,
                source    = otherSource,
                verb      = otherVerb,
                data      = otherData,
                meta      = otherMeta,
                raw       = otherRaw,
                output    = otherOutput,
                tag       = otherTag,
                version   = otherVersion,
                timestamp = otherTimestamp
        )
    }

    /**
     * Access to the raw spark request
     */
    override fun raw(): Any? = call.request

    /**
     * Access to an uploaded file
     */
    override fun getDoc(name: String): Doc? {
        // This can be expensive. So cache it.
        return getFile(name) { stream -> KtorUtils.loadFile(name, stream) }
    }


    override fun getFile(name: String, callback: (InputStream) -> Doc): Doc {
        NOTE.IMPLEMENT("API", "Make this non-blocking")
        val d = runBlocking {
            KtorUtils.loadFile(call, callback)
        }
        return d
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

        fun build(ctx: Context, body: String, call: ApplicationCall, settings: ServerSettings): Request {
            val req = call.request
            val httpUri = req.uri
            val rawUri = if (httpUri.startsWith(settings.prefix)) httpUri.substring(settings.prefix.length) else httpUri

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
            return KtorRequest(
                    call,
                    path = uri,
                    parts = parts,
                    source = Source.Web,
                    verb = method,
                    meta = KtorHeaders(req, ctx.enc),
                    data = KtorParams(body, req, ctx.enc),
                    raw = call.request,
                    tag = Random.uuid()
            )
        }
    }
}

/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.server.ktor

import io.ktor.application.ApplicationCall
import kiit.common.*
import kiit.server.ServerSettings
import io.ktor.request.*
import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.experimental.async
import kiit.common.types.ContentFile
import kiit.requests.Request
import kiit.requests.RequestSupport
import kiit.common.Source
import kiit.common.ext.tail
import kiit.common.utils.Random
import kiit.common.values.Inputs
import kiit.common.values.Metadata
import kiit.context.Context
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
                 val call: ApplicationCall,
        override val path: String,
        override val parts: List<String>,
        override val source: Source,
        override val verb: String,
        override val data: Inputs,
        override val meta: Metadata,
        override val raw: Any? = null,
        override val output: String? = "",
        override val tag: String = "",
        override val version: String = "0",
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
            otherMeta: Metadata,
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
    override fun getDoc(name: String?): ContentFile? {
        NOTE.IMPLEMENT("Server", "Make this non-blocking")
        return runBlocking {
            KtorMultiParts.loadFile(call, name)
        }
    }


    override fun getDoc(name: String?, callback: (InputStream) -> ContentFile): ContentFile {
        NOTE.IMPLEMENT("Server", "Make this non-blocking")
        return runBlocking {
            KtorMultiParts.loadFile(call, name, callback)
        }
    }

    /**
     * Access to an uploaded file
     */
    override fun getFileStream(name: String?):InputStream? {
        NOTE.IMPLEMENT("Server", "Make this non-blocking")
        return runBlocking {
            KtorMultiParts.loadFileStream(call, name)
        }
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
            // 1. no version: /{area}/{api}/{action}
            // 2. w/ version: /1/{area}/{api}/${action}
            val rawParts = uri.split('/')
            val has3PartPath = rawParts.size == 3
            val parts = if(has3PartPath) rawParts else rawParts.tail()
            val version = if(has3PartPath) "0" else rawParts[0]
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
                    source = Source.API,
                    verb = method,
                    meta = KtorHeaders(req, ctx.enc),
                    data = KtorParams(body, req, ctx.enc),
                    raw = call.request,
                    version = version,
                    tag = Random.uuid()
            )
        }
    }
}
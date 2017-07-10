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
import slatekit.common.Random
import slatekit.core.common.AppContext
import slatekit.server.ServerConfig
import spark.Request


object HttpRequest {

    fun build(ctx: AppContext, req: Request, conf: ServerConfig): slatekit.common.Request {
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
                area = parts[0],
                name = parts[1],
                action = parts[2],
                protocol = ApiConstants.ProtocolWeb,
                verb = req.requestMethod().toLowerCase(),
                opts = HttpHeaders(req, ctx.enc),
                args = HttpParams(req, ctx.enc),
                raw = req,
                tag = Random.stringGuid()
        )
    }


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
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

package slatekit.server

import slatekit.apis.ApiContainer
import slatekit.apis.ApiProtocolWeb
import slatekit.apis.ApiReg
import slatekit.apis.core.Auth
import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.success
import slatekit.core.common.AppContext
import slatekit.server.spark.HttpRequest
import slatekit.server.spark.HttpResponse
import spark.Request
import spark.Response
import spark.Spark


class Server(
        val ctx: AppContext,
        val auth: Auth,
        val apis: List<ApiReg>,
        val config: ServerConfig
) {
    val container = ApiContainer(ctx, false, auth, ApiProtocolWeb, apis)


    /**
     * executes the application
     * @return
     */
    fun run(): Result<Any> {

        // Configure
        Spark.port(config.port)

        // Ping/Check
        Spark.get(config.prefix + "/ping", { req, res -> ping(req, res) })

        // Allow all the verbs/routes to hit exec method
        // The exec method will dispatch the request to
        // the corresponding SlateKit API.
        Spark.get(config.prefix + "/*", { req, res -> exec(req, res) })
        Spark.post(config.prefix + "/*", { req, res -> exec(req, res) })
        Spark.put(config.prefix + "/*", { req, res -> exec(req, res) })
        Spark.delete(config.prefix + "/*", { req, res -> exec(req, res) })

        return success(true)
    }


    /**
     * stops the server ( this is not currently accessible on the command line )
     */
    fun stop(): Unit {
        spark.Spark.stop()
    }


    /**
     * pings the server to only get back the datetime.
     * Used for quickly checking a deployment.
     */
    fun ping(req: Request, res: Response): String {
        val result = DateTime.now()
        val text = HttpResponse.json(res, success(result))
        return text
    }


    /**
     * handles the core logic of execute the http request.
     * This is actually accomplished by the SlateKit API Container
     * which handles abstracted Requests and dispatches them to
     * Slate Kit "Protocol Independent APIs".
     */
    fun exec(req: Request, res: Response): String {
        val request = HttpRequest.build(ctx, req, config)
        val result = container.call(request)
        val text = HttpResponse.json(res, result)
        return text
    }
}
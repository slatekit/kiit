package slatekit.server.spark

import slatekit.apis.ApiContainer
import slatekit.common.Context
import slatekit.server.ServerConfig
import slatekit.server.common.Diagnostics
import spark.Request
import spark.Response
import spark.Spark


class KtorHandler(
        val context: Context,
        val config: ServerConfig,
        val container: ApiContainer,
        val diagnostics: Diagnostics
) {

    /**
     * Register the routes (with prefix config.prefix ) that
     * SlateKit will handle when the methods are get | post | put | patch | delete
     */
    fun register(){
        // Allow all the verbs/routes to hit exec method
        // The exec method will dispatch the request to
        // the corresponding SlateKit API.
        Spark.get(config.prefix + "/*", { req, res -> exec(req, res) })
        Spark.post(config.prefix + "/*", { req, res -> exec(req, res) })
        Spark.put(config.prefix + "/*", { req, res -> exec(req, res) })
        Spark.patch(config.prefix + "/*", { req, res -> exec(req, res) })
        Spark.delete(config.prefix + "/*", { req, res -> exec(req, res) })
    }


    /**
     * handles the core logic of execute the http request.
     * This is actually accomplished by the SlateKit API Container
     * which handles abstracted Requests and dispatches them to
     * Slate Kit "Protocol Independent APIs".
     */
    fun exec(req: Request, res: Response): Any {

        // Convert the http request to a SlateKit Request
        val request = SparkRequest.build(context, req, config)

        // Execute the API call
        // The SlateKit ApiContainer will handle the heavy work of
        // 1. Checking routes to area/api/actions ( methods )
        // 2. Validating parameters to methods
        // 3. Decoding request to method parameters
        // 4. Executing the method
        // 5. Handling errors
        val result = container.call(request)

        // Record all diagnostics
        // e.g. logs, track, metrics, event
        diagnostics.record(container, request, result)

        // Finally convert the result back to a HttpResult
        val text = SparkResponse.result(res, result)
        return text
    }
}
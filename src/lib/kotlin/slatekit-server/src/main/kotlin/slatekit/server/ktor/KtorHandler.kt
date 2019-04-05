package slatekit.server.ktor

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.httpMethod
import io.ktor.request.receiveText
import io.ktor.routing.*
import slatekit.apis.ApiHost
import slatekit.common.Context
import slatekit.server.ServerConfig
import slatekit.server.common.Diagnostics
import slatekit.server.common.RequestHandler
import slatekit.server.common.ResponseHandler


class KtorHandler(
        override val context: Context,
        val config: ServerConfig,
        override val container:ApiHost,
        override val diagnostics: Diagnostics,
        override val responses: ResponseHandler
) : RequestHandler {

    override fun register(routes:Routing){
        routes.get(config.prefix + "/*/*/*") {
            exec(call)
        }
        routes.post(config.prefix + "/*/*/*") {
            exec(call)
        }
        routes.put(config.prefix + "/*/*/*") {
            exec(call)
        }
        routes.patch(config.prefix + "/*/*/*") {
            exec(call)
        }
        routes.delete(config.prefix + "/*/*/*") {
            exec(call)
        }
    }

    /**
     * handles the core logic of execute the http request.
     * This is actually accomplished by the SlateKit API Container
     * which handles abstracted Requests and dispatches them to
     * Slate Kit "Protocol Independent APIs".
     */
    override suspend fun exec(call: ApplicationCall) {
        val body = when (call.request.httpMethod) {
            HttpMethod.Post -> call.receiveText()
            HttpMethod.Put -> call.receiveText()
            HttpMethod.Patch -> call.receiveText()
            HttpMethod.Delete -> call.receiveText()
            else -> ""
        }

        // Convert the http request to a SlateKit Request
        val request = KtorRequest.build(context, body, call, config)

        // Execute the API call
        // The SlateKit ApiHost will handle the heavy work of
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
        responses.result(call, result)
    }
}
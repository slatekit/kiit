package kiit.server.core

import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Routing
import kiit.apis.ApiServer
import kiit.context.Context
import kiit.telemetry.Diagnostics
import kiit.requests.Request

interface RequestHandler {
    val context: Context
    val container: ApiServer
    val diagnostics: Diagnostics<Request>
    val responses: ResponseHandler

    fun register(routes: Routing)
    /**
     * handles the core logic of execute the http request.
     * This is actually accomplished by the SlateKit API Container
     * which handles abstracted Requests and dispatches them to
     * Slate Kit "Protocol Independent APIs".
     */
    suspend fun exec(call: ApplicationCall)
}
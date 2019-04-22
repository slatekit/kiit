package slatekit.server.common

import io.ktor.application.ApplicationCall
import io.ktor.routing.Routing
import slatekit.apis.ApiHost
import slatekit.common.Context
import slatekit.common.Diagnostics
import slatekit.common.requests.Request

interface RequestHandler {
    val context: Context
    val container: ApiHost
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
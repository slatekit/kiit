package kiit.apis

import kiit.apis.core.Auth
import kiit.apis.routes.RouteMapping
import kiit.context.Context
import kiit.requests.Request

/**
 * @param host : Host running the Apis
 * @param context: Context ( for env, args, conf, logs, about, etc ), @see[kiit.common.Context]
 * @param request: Abstracted HTTP | CLI | QUEUE request
 * @param target : Code/method associated with this request to execute
 * @param source : Source of the call
 * @param args : Additional arguments supplied by the source
 */
data class ApiRequest(
    val host: ApiServer,
    val auth: Auth?,
    val context: Context,
    val request: Request,
    val target: RouteMapping?,
    val source: Any,
    val args: Map<String, Any>?
)

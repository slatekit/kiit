package kiit.apis

import kiit.apis.core.Auth
import kiit.apis.core.Target
import slatekit.context.Context
import slatekit.requests.Request

/**
 * @param host : Host running the Apis
 * @param context: Context ( for env, args, conf, logs, about, etc ), @see[slatekit.common.Context]
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
    val target: Target?,
    val source: Any,
    val args: Map<String, Any>?
)

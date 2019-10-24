package slatekit.apis

import slatekit.apis.core.Target
import slatekit.common.Context
import slatekit.common.requests.Request

/**
 * @param host   : Host running the Apis
 * @param context: Context ( for env, args, conf, logs, about, etc ), @see[slatekit.common.Context]
 * @param request: Abstracted HTTP | CLI | QUEUE request
 * @param target : Code/method associated with this request to execute
 * @param source : Source of the call
 * @param args   : Additional arguments supplied by the source
 */
data class ApiRequest(val host: ApiHost,
                      val context: Context,
                      val request: Request,
                      val target: Target?,
                      val source: Any,
                      val args: Map<String, Any>?)
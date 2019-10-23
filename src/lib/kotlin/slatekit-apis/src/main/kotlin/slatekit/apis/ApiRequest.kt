package slatekit.apis

import slatekit.apis.core.Action
import slatekit.common.Context
import slatekit.common.requests.Request

/**
 * @param context: Context of the call @see[slatekit.common.Context]
 * @param request: Api request
 * @param action : Code/method associated with this request to execute
 * @param source : Source of the call
 * @param args   : Additional arguments supplied by the source
 */
data class ApiRequest(val context: Context,
                      val request: Request,
                      val target: ApiRef?,
                      val source: Any,
                      val args: Map<String, Any>?)
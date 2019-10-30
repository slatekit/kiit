package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.common.Ignore
import slatekit.common.log.Logger
import slatekit.functions.Input
import slatekit.functions.Output
import slatekit.results.Outcome

/**
 * Checks that the route/path is valid in that it has an area/api
 */
class Afters : Output<ApiRequest, ApiResult> {

    @Ignore
    override suspend fun process(raw:ApiRequest, req:Outcome<ApiRequest>, res: Outcome<ApiResult>): Outcome<ApiResult> {
        req.onSuccess {
            val inst = it.target?.instance
            if (inst is slatekit.functions.middleware.After<*, *>) {
                val filterHook = inst as slatekit.functions.middleware.After<ApiRequest, ApiResult>
                filterHook.onAfter(raw, req, res)
            }
        }
        return res
    }
}

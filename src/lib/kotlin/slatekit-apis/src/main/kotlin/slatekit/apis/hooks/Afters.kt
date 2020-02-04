package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.common.Ignore
import slatekit.policy.Output
import slatekit.results.Outcome

/**
 * Checks that the route/path is valid in that it has an area/api
 */
class Afters : Output<ApiRequest, ApiResult> {

    @Ignore
    override suspend fun process(raw:ApiRequest, i:Outcome<ApiRequest>, o: Outcome<ApiResult>): Outcome<ApiResult> {
        i.onSuccess {
            val inst = it.target?.instance
            if (inst is slatekit.policy.middleware.After<*, *>) {
                val filterHook = inst as slatekit.policy.middleware.After<ApiRequest, ApiResult>
                filterHook.after(raw, i, o)
            }
        }
        return o
    }
}

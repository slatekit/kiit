package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.common.Ignore
import slatekit.policy.Input
import slatekit.results.Outcome
import slatekit.results.flatMap

/**
 * Checks that the route/path is valid in that it has an area/api
 */
class Befores : Input<ApiRequest> {

    @Ignore
    override suspend fun process(i: Outcome<ApiRequest>): Outcome<ApiRequest> {
        val filtered = i.flatMap { applyFilter(it, i) }
        filtered.onSuccess { applyBefore(it, i) }
        return filtered
    }


    private suspend fun applyFilter(apiReq:ApiRequest, req:Outcome<ApiRequest>): Outcome<ApiRequest> {
        val inst = apiReq.target?.instance
        val result = when(inst) {
            is slatekit.policy.middleware.Filter<*> -> {
                val filterHook = inst as slatekit.policy.middleware.Filter<ApiRequest>
                filterHook.filter(apiReq)
            }
            else -> req
        }
        return result
    }


    private suspend fun applyBefore(apiReq:ApiRequest, req:Outcome<ApiRequest>): Outcome<ApiRequest> {
        val inst = apiReq.target?.instance
        if(inst is slatekit.policy.middleware.Before<*>) {
            val beforeHook = inst as slatekit.policy.middleware.Before<ApiRequest>
            beforeHook.before(apiReq)
        }
        return req
    }
}

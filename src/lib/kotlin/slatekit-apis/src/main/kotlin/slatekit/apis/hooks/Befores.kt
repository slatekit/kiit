package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.common.Ignore
import slatekit.functions.Input
import slatekit.results.Outcome
import slatekit.results.flatMap

/**
 * Checks that the route/path is valid in that it has an area/api
 */
class Befores : Input<ApiRequest> {

    @Ignore
    override suspend fun process(req: Outcome<ApiRequest>): Outcome<ApiRequest> {
        val filtered = req.flatMap { applyFilter(it, req) }
        filtered.onSuccess { applyBefore(it, req) }
        return filtered
    }


    private suspend fun applyFilter(apiReq:ApiRequest, req:Outcome<ApiRequest>): Outcome<ApiRequest> {
        val inst = apiReq.target?.instance
        val result = when(inst) {
            is slatekit.functions.middleware.Filter<*> -> {
                val filterHook = inst as slatekit.functions.middleware.Filter<ApiRequest>
                filterHook.onFilter(apiReq)
            }
            else -> req
        }
        return result
    }


    private suspend fun applyBefore(apiReq:ApiRequest, req:Outcome<ApiRequest>): Outcome<ApiRequest> {
        val inst = apiReq.target?.instance
        if(inst is slatekit.functions.middleware.Before<*>) {
            val beforeHook = inst as slatekit.functions.middleware.Before<ApiRequest>
            beforeHook.onBefore(apiReq)
        }
        return req
    }
}

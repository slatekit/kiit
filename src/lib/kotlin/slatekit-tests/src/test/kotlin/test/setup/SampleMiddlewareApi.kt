package test.setup

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.apis.support.HooksSupport
import slatekit.results.*
import slatekit.results.builders.Outcomes


open class SampleMiddlewareApi() : HooksSupport {

    // Used for demo/testing purposes
    var onBeforeHookCount = mutableListOf<ApiRequest>()
    var onAfterHookCount = mutableListOf<ApiRequest>()


    /**
     * Hook for before this api handles any request
     */
    override suspend fun onBefore(req:ApiRequest) {
        onBeforeHookCount.add(req)
    }


    /**
     * Hook for after this api handles any request
     */
    override suspend fun onAfter(req:ApiRequest, res:Outcome<ApiResult>) {
        onAfterHookCount.add(req)
    }


    /**
     * Hook to first filter a request before it is handled by this api.
     */
    override suspend fun onFilter(req:ApiRequest): Outcome<Boolean>  {
        return if(req.request.action.startsWith("hi")) {
            Outcomes.errored(Exception("filtered out"), Codes.IGNORED)
        } else {
            Outcomes.success(true)
        }
    }


    override suspend fun onError(req: ApiRequest, res:Outcome<ApiResult>) {

    }


    fun hi(): String = "hi world"


    fun hello(): String = "hello world"
}
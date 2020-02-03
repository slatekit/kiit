package test.setup

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.apis.support.HooksSupport
import slatekit.results.*
import slatekit.results.builders.Outcomes


open class SampleMiddlewareApi : HooksSupport {

    // Used for demo/testing purposes
    var onBeforeHookCount = mutableListOf<ApiRequest>()
    var onAfterHookCount = mutableListOf<ApiRequest>()
    var onErrorHookCount = mutableListOf<Outcome<ApiRequest>>()


    /**
     * Hook for before this api handles any request
     */
    override suspend fun before(req:ApiRequest) {
        onBeforeHookCount.add(req)
    }


    /**
     * Hook to first filter a request before it is handled by this api.
     */
    override suspend fun filter(req:ApiRequest): Outcome<ApiRequest>  {
        return if(req.request.action.startsWith("hi")) {
            Outcomes.errored(Exception("filtered out"))
        } else {
            Outcomes.success(req)
        }
    }


    /**
     * Hook for after this api handles any request
     */
    override suspend fun after(raw:ApiRequest, req: Outcome<ApiRequest>, res:Outcome<ApiResult>) {
        onAfterHookCount.add(raw)
    }


    override suspend fun failed(raw:ApiRequest, req: Outcome<ApiRequest>, res:Outcome<ApiResult>) {
        onErrorHookCount.add(req)
    }


    fun hi(): String = "hi world"


    fun unexpected(): String {
        throw Exception("Testing failed middleware")
    }


    fun errored(): Outcome<String> {
        return Outcomes.errored("test failed")
    }


    fun hello(): String = "hello world"
}
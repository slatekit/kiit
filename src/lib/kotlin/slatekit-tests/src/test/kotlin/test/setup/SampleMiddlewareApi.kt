package test.setup

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.apis.Middleware
import kiit.results.*
import kiit.results.builders.Outcomes


open class SampleMiddlewareApi : Middleware {

    // Used for demo/testing purposes
    var middlewareHook = mutableListOf<ApiRequest>()


    fun hi(): String = "hi world"


    fun unexpected(): String {
        throw Exception("Testing failed middleware")
    }


    fun errored(): Outcome<String> {
        return Outcomes.errored("test failed")
    }


    fun hello(): String = "hello world"


    override suspend fun process(req: ApiRequest, next: suspend (ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult> {
        middlewareHook.add(req)
        return next(req)
    }
}
package test.setup

import kiit.apis.*
import kiit.results.*
import kiit.results.builders.Outcomes


@Api(area = "app", name = "SampleMiddleware", desc = "api to access and manage users 3", auth = AuthModes.NONE)
open class SampleMiddlewareApi : Middleware {

    // Used for demo/testing purposes
    var middlewareHook = mutableListOf<ApiRequest>()


    @Action()
    fun hi(): String = "hi world"


    @Action()
    fun unexpected(): String {
        throw Exception("Testing failed middleware")
    }


    @Action()
    fun errored(): Outcome<String> {
        return Outcomes.errored("test failed")
    }


    @Action()
    fun hello(): String = "hello world"


    override suspend fun process(req: ApiRequest, next: suspend (ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult> {
        middlewareHook.add(req)
        return next(req)
    }
}
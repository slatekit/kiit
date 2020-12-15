package test.setup

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.results.*
import slatekit.results.builders.Outcomes


open class SampleMiddlewareApi {

    // Used for demo/testing purposes
    var onBeforeHookCount = mutableListOf<ApiRequest>()
    var onAfterHookCount = mutableListOf<ApiRequest>()
    var onErrorHookCount = mutableListOf<Outcome<ApiRequest>>()


    fun hi(): String = "hi world"


    fun unexpected(): String {
        throw Exception("Testing failed middleware")
    }


    fun errored(): Outcome<String> {
        return Outcomes.errored("test failed")
    }


    fun hello(): String = "hello world"
}
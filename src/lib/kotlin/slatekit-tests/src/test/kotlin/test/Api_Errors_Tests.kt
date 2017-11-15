package test

import org.junit.Test
import slatekit.apis.ApiReg
import slatekit.apis.ApiContainerCLI
import slatekit.apis.core.Errors
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.results.ResultFuncs.failure
import slatekit.sampleapp.core.apis.SampleErrorsApi


class Api_Errors_Tests : ApiTestsBase() {


    @Test fun can_use_error_codes() {
        val number = "abc"
        val apis = ApiContainerCLI(ctx, apis = listOf(ApiReg(SampleErrorsApi(true), declaredOnly = false)), auth = null)
        val result = apis.call("", "SampleErrors", "parseNumberWithResults", "", mapOf(), mapOf("text" to number))
        assert(!result.success)
        assert(result.isFailure)
        assert(result.msg == "$number is not a valid number")
    }


    @Test fun can_use_error_handling_at_api_level() {
        val number = "abc"
        val apis = ApiContainerCLI(ctx, apis = listOf(ApiReg(SampleErrorsApi(true), declaredOnly = false)), auth = null)
        val result = apis.call("", "SampleErrors", "parseNumberWithExceptions", "", mapOf(), mapOf("text" to number))
        assert(!result.success)
        assert(result.isUnexpectedError)
        assert(result.msg == "unexpected error in api")
    }


    @Test fun can_use_error_handling_at_container_level() {
        var exRef:Exception? = null
        val callback = { ctx: Context, req: Request, source:Any, ex:Exception -> exRef = ex; failure<Int>(ex.message) }
        val number = "abc"
        val apis = ApiContainerCLI(ctx, errors = Errors( callback ), apis = listOf(ApiReg(SampleErrorsApi(false), declaredOnly = false)), auth = null)
        val result = apis.call("", "SampleErrors", "parseNumberWithExceptions", "", mapOf(), mapOf("text" to number))
        assert(!result.success)
        assert(result.isFailure)
        assert(exRef != null)
    }
}
package test.setup

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.common.*
import slatekit.common.requests.Request
import slatekit.results.Notice
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.Try


@Api(area = "app", name = "retry", desc = "sample to test features of Slate Kit APIs", roles= "", auth = "app-roles", verb = "*", protocol = "*")
open class SampleRetryApi(val err:slatekit.apis.Error) : slatekit.apis.Error {


    private var counter = -1


    /**
     * Error-handling using the Result<T> object to model
     * successes and failures for all scenarios
     */
    @Action(desc = "tests a retry attempt with the error queue", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun test(text:String): Notice<Int> {
        counter += 1
        if(counter % 2 == 0){
            throw Exception("testing retry")
        }
        return Success(counter, msg = text)
    }


    @Ignore
    override suspend fun onError(req: ApiRequest, res: Outcome<ApiResult>) {
        return err.onError(req, res)
    }
}



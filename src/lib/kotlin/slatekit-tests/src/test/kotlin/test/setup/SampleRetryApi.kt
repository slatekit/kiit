package test.setup

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.common.*
import slatekit.results.Notice
import slatekit.results.Outcome
import slatekit.results.Success


@Api(area = "app", name = "retry", desc = "sample to test features of Slate Kit APIs")
open class SampleRetryApi()  {


    private var counter = -1


    /**
     * Error-handling using the Result<T> object to model
     * successes and failures for all scenarios
     */
    @Action(desc = "tests a retry attempt with the error queue")
    fun test(text:String): Notice<Int> {
        counter += 1
        if(counter % 2 == 0){
            throw Exception("testing retry")
        }
        return Success(counter, msg = text)
    }
}



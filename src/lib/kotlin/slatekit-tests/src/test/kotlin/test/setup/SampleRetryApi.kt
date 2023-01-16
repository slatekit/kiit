package test.setup

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.ApiRequest
import kiit.apis.ApiResult
import kiit.common.*
import kiit.results.Notice
import kiit.results.Outcome
import kiit.results.Success


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



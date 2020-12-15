package test.setup

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.common.*
import slatekit.common.requests.Request
import slatekit.common.validations.ValidationFuncs
import slatekit.results.Notice
import slatekit.results.Outcome
import slatekit.results.Try
import slatekit.results.builders.Notices
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries


open class SampleErrorsApi  {

    /**
     * Error-handling using the Result<T> object to model
     * successes and failures for all scenarios
     */
    fun parseNumberWithResults(text:String): Notice<Int> {

        return if(text.isNullOrEmpty()) {
            Notices.invalid("You must supply a non-empty string")
        }
        else if(!ValidationFuncs.isNumeric(text)){
            Notices.errored("$text is not a valid number")
        }
        else {
            Notices.success(text.toInt(), msg = "You supplied a valid number")
        }
    }


    /**
     * Error-handling exceptions for bad inputs.
     * NOTE: This class implements the ApiWithMiddleWare interface
     * and has the "isErrorEnabled" flag set to true which will result
     * in the "onException" method handling the exception here.
     */
    fun parseNumberWithExceptions(text:String): Int {

        // This will throw exception for non-numbers
        val num = text.toInt()
        return num
    }
}



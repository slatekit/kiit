package test.setup

import slatekit.apis.support.ApiWithMiddleware
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.UNEXPECTED_ERROR
import slatekit.common.validations.ValidationFuncs


open class SampleErrorsApi(enableErrorHandling:Boolean) : ApiWithMiddleware {



    /**
     * Error-handling using the Result<T> object to model
     * successes and failures for all scenarios
     */
    fun parseNumberWithResults(text:String): Result<Int> {

        return if(text.isNullOrEmpty()) {
            badRequest("You must supply a non-empty string")
        }
        else if(!ValidationFuncs.isNumeric(text)){
            failure("$text is not a valid number")
        }
        else {
            success(text.toInt(), "You supplied a valid number")
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


    override fun onError(ctx: Context, req: Request, target:Any, source: Any, ex: Exception?, args: Map<String, Any>?): Result<Any>{
        return ResultFuncs.failureWithCode(UNEXPECTED_ERROR, msg = "unexpected error in api", err = ex)
    }

}
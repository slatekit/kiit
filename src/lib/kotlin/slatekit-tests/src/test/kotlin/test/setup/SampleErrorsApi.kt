package test.setup

import slatekit.common.checks.Check
import slatekit.results.Notice
import slatekit.results.builders.Notices


open class SampleErrorsApi  {

    /**
     * Error-handling using the Result<T> object to model
     * successes and failures for all scenarios
     */
    fun parseNumberWithResults(text:String): Notice<Int> {

        return if(text.isNullOrEmpty()) {
            Notices.invalid("You must supply a non-empty string")
        }
        else if(!Check.isNumeric(text)){
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



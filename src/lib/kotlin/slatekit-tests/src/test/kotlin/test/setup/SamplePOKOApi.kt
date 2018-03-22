package test.setup

import slatekit.common.DateTime
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success


/**
 * Sample 1:
 * This is the simplest example of APIs in Slate Kit.
 * APIs are designed to be "Universal" and "Protocol Independent" which means
 * that these can be hosted as Web/HTTP APIs and CLI ( Command Line )
 *
 * NOTES:
 * 1. POKO       : Plain old kotlin object without any framework code
 * 2. Actions    : Only public methods declared in this class will be exposed
 * 3. Protocol   : This API can be accessed via HTTP and/or on the CLI
 * 4. Arguments  : Method params are automatically loaded
 * 5. Annotations: This examples has 0 annotations, but you can add them
 *                 to explicitly declare and configure the APIs
 */
open class SamplePOKOApi {
    var count = 0

    /**
     * By default prefixed with
     * 1. "get"    uses http GET
     * 2. "delete" uses http DELETE
     * 3. "create" uses http POST
     * 4. "update" uses http PUT
     * 5. "patch"  uses http PATCH
     *
     * Everything else defaults to POST
    */
    fun getTime(): String = DateTime.now().toString()


    fun getCounter(): Int = ++count


    fun hello(greeting: String): String = "$greeting back"


    fun request(req: Request): String = req.data?.getString("greeting") ?: "hi"


    fun response(guess:Int): Result<Int> = if(guess == 1) success(1, "Correct") else failure("Try again")


    /**
     * These are not exposed as API actions/endpoints
     */
    protected fun getEmail():String = "jane@abc.com"
    private fun getSsn(): String = "123-45-6789"
}
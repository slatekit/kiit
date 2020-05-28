package test.setup

import slatekit.common.DateTime
import slatekit.common.requests.Request
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success


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


    fun request(req: Request): String {
        val result = req.data.getString("greeting") ?: "hi"
        return result
    }


    fun response(guess:Int): Notice<Int> = if(guess == 1) Success(1, msg = "Correct") else Failure("Try again")


    /**
     * These are not exposed as API actions/endpoints
     */
    protected fun getEmail():String = "jane@abc.com"


    private fun getSsn(): String = "123-45-6789"
}

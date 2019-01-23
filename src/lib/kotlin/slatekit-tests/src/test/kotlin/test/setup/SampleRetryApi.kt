package test.setup

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.*
import slatekit.common.requests.Request


@Api(area = "app", name = "retry", desc = "sample to test features of Slate Kit APIs", roles= "", auth = "app-roles", verb = "*", protocol = "*")
open class SampleRetryApi(val err:slatekit.apis.middleware.Error) : slatekit.apis.middleware.Error {

    private var counter = -1


    /**
     * Error-handling using the Result<T> object to model
     * successes and failures for all scenarios
     */
    @ApiAction(desc = "tests a retry attempt with the error queue", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun test(text:String): ResultMsg<Int> {
        counter += 1
        if(counter % 2 == 0){
            throw Exception("testing retry")
        }
        return Success(counter, msg = text)
    }


    @Ignore
    override fun onError(ctx: Context, req: Request, target:Any, source: Any, ex: Exception?, args: Map<String, Any>?): ResultEx<Any> {
        return err.onError(ctx, req, target, source, ex, args)
    }
}



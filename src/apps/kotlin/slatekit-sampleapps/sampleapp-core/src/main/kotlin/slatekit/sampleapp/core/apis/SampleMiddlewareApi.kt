package slatekit.sampleapp.core.apis


import slatekit.apis.core.Action
import slatekit.apis.support.ApiWithMiddleware
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.ResultMsg
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.sampleapp.core.models.User


open class SampleMiddlewareApi() : ApiWithMiddleware {



    // Used for demo/testing purposes
    var _user: User = User(0, "", "", "", "", "", "", "", "")
    var onBeforeHookCount = mutableListOf<Request>()
    var onAfterHookCount = mutableListOf<Request>()


    /**
     * Hook for before this api handles any request
     */
    override fun onBefore(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?): Unit {
        onBeforeHookCount.add(req)
    }


    /**
     * Hook for after this api handles any request
     */
    override fun onAfter(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?): Unit {
        onAfterHookCount.add(req)
    }


    /**
     * Hook to first filter a request before it is handled by this api.
     */
    override fun onFilter(ctx: Context, req: Request, source: Any, args: Map<String, Any>?): ResultMsg<Any> {
        return if(req.action.startsWith("hi")) {
            badRequest<Boolean>("filtered out")
        } else {
            ResultFuncs.yes()
        }
    }


    fun hi(): String = "hi world"


    fun hello(): String = "hello world"
}
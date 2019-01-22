package test.setup

import slatekit.apis.core.Action
import slatekit.apis.support.ApiWithMiddleware
import slatekit.common.*
import slatekit.common.requests.Request
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.yes


open class SampleMiddlewareApi() : ApiWithMiddleware {

    // Used for demo/testing purposes
    var onBeforeHookCount = mutableListOf<Request>()
    var onAfterHookCount = mutableListOf<Request>()


    /**
     * Hook for before this api handles any request
     */
    override fun onBefore(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?) {
        onBeforeHookCount.add(req)
    }


    /**
     * Hook for after this api handles any request
     */
    override fun onAfter(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?) {
        onAfterHookCount.add(req)
    }


    /**
     * Hook to first filter a request before it is handled by this api.
     */
    override fun onFilter(ctx: Context, req: Request, source: Any, args: Map<String, Any>?): ResultMsg<Any>  {
        return if(req.action.startsWith("hi")) {
            badRequest<Boolean>("filtered out")
        } else {
            yes()
        }
    }


    override fun onError(ctx: Context, req: Request, target:Any, source: Any, ex: Exception?, args: Map<String, Any>?): ResultEx<Any> {
        return Success("")
    }


    fun hi(): String = "hi world"


    fun hello(): String = "hello world"
}
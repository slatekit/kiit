package slatekit.apis.middleware

import slatekit.common.Context
import slatekit.common.InputsUpdateable
import slatekit.common.Request
import slatekit.common.info.About


/**
 * A "Rewriter" based middle-ware allows allows for rewriting the API path/call
 *
 * @param about  : Info about the the middleware
 * @param route  : The route pattern to apply this middleware to
 */
open class Rewriter(override val about: About, val route: Match) : Middleware() {


    /**
     * Rewrites the calls and returns a new request
     * @param ctx   : The application context
     * @param req   : The request
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args  : Additional arguments supplied by the source
     */
    open fun rewrite(ctx: Context, req: Request, source: Any, args: Map<String, Any>): Request {
        return req
    }


    fun rewriteAction(req: Request, newAction:String, format:String? = null): Request {
        // Get the first and second part
        val first = req.parts[0]
        val second = req.parts[1]
        return req.copy(
                path = "$first/$second/$newAction",
                parts = listOf(first, second, newAction),
                output = format ?: req.output
        )
    }


    fun rewriteActionWithParam(req: Request, newAction:String, key:String, value:String): Request {
        // Get the first and second part
        val first = req.parts[0]
        val second = req.parts[1]
        return req.copy(
                path = "$first/$second/$newAction",
                parts = listOf(first, second, newAction),
                data = (req.data as InputsUpdateable).add(key, value)
        )
    }
}

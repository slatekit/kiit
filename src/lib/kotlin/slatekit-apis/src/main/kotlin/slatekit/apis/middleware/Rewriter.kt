package slatekit.apis.middleware

import slatekit.common.Context
import slatekit.common.Inputs
import slatekit.common.InputsUpdateable
import slatekit.common.requests.Request
import slatekit.common.requests.Source

/**
 * A "Rewriter" based middle-ware allows allows for rewriting the API path/call
 *
 */
open class Rewriter : Middleware {

    /**
     * Rewrites the calls and returns a new request
     * @param ctx : The application context
     * @param req : The request
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args : Additional arguments supplied by the source
     */
    open fun rewrite(ctx: Context, req: Request, source: Any, args: Map<String, Any>): Request {
        return req
    }

    fun rewriteAction(req: Request, newAction: String, format: String? = null): Request {
        // Get the first and second part
        val first = req.parts[0]
        val second = req.parts[1]
        return req.clone(
                "$first/$second/$newAction",
                listOf(first, second, newAction),
                req.source,
                req.verb,
                req.data,
                req.meta,
                req.raw,
                format ?: req.output,
                req.tag,
                req.version,
                req.timestamp
        )
    }

    fun rewriteActionWithParam(req: Request, newAction: String, key: String, value: String): Request {
        // Get the first and second part
        val first = req.parts[0]
        val second = req.parts[1]
        return req.clone(
                "$first/$second/$newAction",
                listOf(first, second, newAction),
                req.source,
                req.verb,
                (req.data as InputsUpdateable).add(key, value),
                req.meta,
                req.raw,
                req.output,
                req.tag,
                req.version,
                req.timestamp
        )
    }
}

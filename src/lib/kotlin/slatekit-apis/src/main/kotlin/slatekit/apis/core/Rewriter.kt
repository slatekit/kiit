package slatekit.apis.core

import slatekit.apis.ApiRequest
import slatekit.common.Context
import slatekit.common.InputsUpdateable
import slatekit.common.requests.Request
import slatekit.functions.middleware.Middleware

/**
 * A "Rewriter" based middle-ware allows allows for rewriting the API path/call
 *
 */
open class Rewriter : Middleware {

    open fun rewrite(req:ApiRequest): ApiRequest {
        return req
    }


    fun rewriteAction(request: ApiRequest, newAction: String, format: String? = null): ApiRequest {
        val req = request.request
        // Get the first and second part
        val first = req.parts[0]
        val second = req.parts[1]
        val updated = req.clone(
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
        return request.copy(request = updated)
    }

    fun rewriteActionWithParam(request: ApiRequest, newAction: String, key: String, value: String): ApiRequest {
        val req = request.request
        // Get the first and second part
        val first = req.parts[0]
        val second = req.parts[1]
        val updated = req.clone(
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
        return request.copy(request = updated)
    }
}

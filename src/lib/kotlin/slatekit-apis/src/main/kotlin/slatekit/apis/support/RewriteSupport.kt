package slatekit.apis.support

import slatekit.apis.ApiRequest
import slatekit.common.values.InputsUpdateable

interface RewriteSupport {

    fun rewrite(request: ApiRequest, newAction: String, format: String? = null): ApiRequest {
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

    fun rewriteWithParam(request: ApiRequest, newAction: String, key: String, value: String): ApiRequest {
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

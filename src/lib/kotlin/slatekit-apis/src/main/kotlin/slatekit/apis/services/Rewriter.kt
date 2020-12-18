package slatekit.apis.services

import slatekit.apis.ApiRequest

interface Rewriter {
    suspend fun process(req: ApiRequest): ApiRequest
}

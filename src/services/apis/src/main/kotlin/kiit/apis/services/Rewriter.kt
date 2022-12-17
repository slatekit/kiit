package kiit.apis.services

import kiit.apis.ApiRequest

interface Rewriter {
    suspend fun process(req: ApiRequest): ApiRequest
}

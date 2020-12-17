package slatekit.apis

import slatekit.results.Outcome

interface Middleware {
    suspend fun process(req: ApiRequest, next:suspend(ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult>
}

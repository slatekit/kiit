package kiit.apis

import kiit.results.Outcome


interface Middleware {
    suspend fun process(req: ApiRequest, next:suspend(ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult>

    companion object {
        suspend fun process(req:ApiRequest, index:Int, middleware: List<Middleware>, next: suspend (ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult> {
            // Bounds/List check
            if(index < 0 || index >= middleware.size || middleware.isEmpty())
                return next(req)

            // Last one
            if(index == middleware.size - 1)
                return middleware[index].process(req, next)

            val curr = middleware[index]
            return curr.process(req) {
                process(it, index + 1, middleware, next)
            }
        }
    }
}

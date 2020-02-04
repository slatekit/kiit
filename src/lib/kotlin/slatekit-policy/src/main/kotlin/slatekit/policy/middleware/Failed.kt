package slatekit.policy.middleware

import slatekit.common.*
import slatekit.results.Err
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success

/**
 * A "Filter" based middle-ware that either allows/disallows an API call to proceed
 *
 * NOTE: This is applied before conversion of request parameters( e.g. JSON )
 * to action parameters.
 */
interface Failed<TReq, TRes> : Middleware {
    /**
     * Middleware hook for after a request is made
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun failed(raw: TReq, req: Outcome<TReq>, res: Outcome<TRes>) {
    }

    /**
     * Middleware hook for handling a failed request
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun failedRequest(raw: TReq, req: Failure<Err>) {
    }

    /**
     * Middleware hook for handling a failed result
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun failedResult(raw: TReq, req: Success<TReq>, res: Failure<Err>) {
    }

    companion object {
        /**
         * Middleware hook for after a request is made
         * @param req : The request for the call
         * @param res : The result of the call
         */
        @Ignore
        suspend fun <TReq, TRes> handle(middleware:Failed<TReq, TRes>, raw: TReq, req: Outcome<TReq>, res: Outcome<TRes>) {
            when (req) {
                is Failure -> {
                    middleware.failed(raw, req, res)
                    middleware.failedRequest(raw, req)
                }
                is Success -> {
                    when (res) {
                        is Failure -> {
                            middleware.failed(raw, req, res)
                            middleware.failedResult(raw, req, res)
                        }
                        is Success -> {}
                    }
                }
            }
        }
    }
}




package kiit.policy.hooks

import kiit.common.*
import kiit.results.Err
import kiit.results.Failure
import kiit.results.Outcome

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
    suspend fun failed(req: TReq, res: Outcome<TRes>) {
    }

    /**
     * Middleware hook for handling a failed request
     * @param raw : The request for the call
     * @param req : The result of the call
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
    suspend fun failedResult(req: TReq, res: Failure<Err>) {
    }
}




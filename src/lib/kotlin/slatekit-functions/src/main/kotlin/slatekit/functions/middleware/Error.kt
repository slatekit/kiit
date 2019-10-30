package slatekit.functions.middleware

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
interface Error<TReq, TRes> : Middleware {
    /**
     * Middleware hook for after a request is made
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun onDone(raw: TReq, req: Outcome<TReq>, res: Outcome<TRes>) {
        when (req) {
            is Failure -> onFailedRequest(raw, req)
            is Success -> {
                when (res) {
                    is Failure -> onFailedResult(raw, req, res)
                    is Success -> onSuccess(raw, req, res)
                }
            }
        }
    }

    /**
     * Middleware hook for handling a successful request/result
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun onSuccess(raw: TReq, req: Success<TReq>, res: Success<TRes>){
    }

    /**
     * Middleware hook for handling a failed request
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun onFailedRequest(raw: TReq, req: Failure<Err>) {
    }

    /**
     * Middleware hook for handling a failed result
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun onFailedResult(raw: TReq, req: Success<TReq>, res: Failure<Err>) {
    }
}




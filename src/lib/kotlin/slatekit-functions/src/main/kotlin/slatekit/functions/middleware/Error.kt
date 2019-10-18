package slatekit.functions.middleware

import slatekit.common.*
import slatekit.results.Outcome

/**
 * A "Filter" based middle-ware that either allows/disallows an API call to proceed
 *
 * NOTE: This is applied before conversion of request parameters( e.g. JSON )
 * to action parameters.
 */
interface Error<TReq, TRes> : Middleware {

    /**
     * Handles the error
     * @param req : The request ( e.g. call )
     * @param res : The response of the call
     * @sample: slatekit.results.Success("")
     */
    @Ignore
    suspend fun onError(req: TReq, res:Outcome<TRes>): Outcome<TRes>
}
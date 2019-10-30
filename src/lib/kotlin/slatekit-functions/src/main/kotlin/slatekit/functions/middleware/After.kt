package slatekit.functions.middleware

import slatekit.common.Ignore
import slatekit.results.Outcome

/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of a call, without any modification to the life-cycle/flow.
 */
interface After<TReq, TRes> {
    /**
     * Middleware hook for after a request is made
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun onAfter(raw:TReq, req: Outcome<TReq>, res: Outcome<TRes>)
}

package kiit.policy.hooks

import kiit.common.Ignore
import kiit.results.Outcome

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
    suspend fun after(req:TReq, res: Outcome<TRes>)
}




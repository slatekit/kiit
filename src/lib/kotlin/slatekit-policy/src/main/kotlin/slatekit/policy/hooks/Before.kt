package slatekit.policy.hooks

import slatekit.common.Ignore

/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of a call, without any modification to the life-cycle/flow.
 */
interface Before<TReq> : Middleware {

    /**
     * Middleware hook for after a request is made
     * @param req : The request for the call
     */
    @Ignore
    suspend fun before(req: TReq)
}

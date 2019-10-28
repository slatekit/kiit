package slatekit.functions.middleware

import slatekit.common.Ignore
import slatekit.common.Status
import slatekit.common.metrics.Counters
import slatekit.functions.policy.Policy
import slatekit.functions.policy.Every
import slatekit.functions.policy.Limit
import slatekit.results.Outcome
import slatekit.results.getOrElse

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
    suspend fun onAfter(req: TReq, res:Outcome<TRes>)
}
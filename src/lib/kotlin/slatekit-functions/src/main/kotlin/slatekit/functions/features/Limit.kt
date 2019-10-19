package slatekit.functions.features

import slatekit.common.metrics.Counters
import slatekit.results.Outcome

class Limit<TReq, TRes>(val limit:Long,
                        val stats:(TReq) -> Counters,
                        val op:suspend(TReq, Outcome<TRes>) -> Outcome<TRes>) : Feature<TReq, TRes> {

    override suspend fun handle(req:TReq, res: Outcome<TRes>): Outcome<TRes> {
        val counters = stats(req)
        val atLimit = counters.totalProcessed() >= limit
        return if(atLimit) {
            op(req, res)
        } else {
            res
        }
    }
}
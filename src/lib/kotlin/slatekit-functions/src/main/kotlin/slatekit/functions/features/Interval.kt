package slatekit.functions.features

import slatekit.results.Outcome
import java.util.concurrent.atomic.AtomicLong

class Interval<TReq, TRes>(val limit:Long, val op:suspend(TReq, Outcome<TRes>) -> Unit ) : Feature<TReq, TRes> {
    private val count = AtomicLong(0L)

    override suspend fun handle(req:TReq, res: Outcome<TRes>): Outcome<TRes> {
        val curr = count.incrementAndGet()
        if(curr >= limit) {
            op(req, res)
            count.set(0L)
        }
        return res
    }
}
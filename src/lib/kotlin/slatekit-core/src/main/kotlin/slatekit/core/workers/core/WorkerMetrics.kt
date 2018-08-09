package slatekit.core.workers.core

import slatekit.common.DateTime
import slatekit.common.ResultMsg
import slatekit.common.results.ResultFuncs
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Metrics for each worker.
 * This captures info about how many times a worker has run, the last run time,
 * error count and more.
 */
open class WorkerMetrics(val started: DateTime) {

    val totalRequests  = AtomicLong(0L)
    val totalFiltered  = AtomicLong(0L)
    val totalErrored   = AtomicLong(0L)
    val totalSucccess  = AtomicLong(0L)
    val lastRequest    = AtomicReference<Any>("not started")
    val lastFiltered   = AtomicReference<Any>("not started")
    val lastErrored    = AtomicReference<Pair<Any, ResultMsg<Any>>>(Pair("not started", ResultFuncs.failure("not started")))
    val lastSuccess    = AtomicReference<Pair<Any, ResultMsg<Any>>>(Pair("not started", ResultFuncs.failure("not started")))


    fun request(req:Any) {
        totalRequests.incrementAndGet()
        lastRequest.set(req)
    }


    fun success(request:Any, result:ResultMsg<Any>) {
        totalSucccess.incrementAndGet()
        lastSuccess.set(Pair(request, result))
    }


    fun filtered(request:Any) {
        totalFiltered.incrementAndGet()
        lastFiltered.set(request)
    }


    fun errored(request:Any, result:ResultMsg<Any>) {
        totalErrored.incrementAndGet()
        lastErrored.set(Pair(request, result))
    }
}
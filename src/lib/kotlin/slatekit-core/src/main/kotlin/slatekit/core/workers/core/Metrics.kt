package slatekit.core.workers.core

import slatekit.common.DateTime
import slatekit.common.ResultMsg
import slatekit.common.results.ResultFuncs
import slatekit.core.workers.Job
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Metrics track the total and last requests, successes, failures, filtered ( optional )
 * This is currently used for Workers to track totals and last results.
 */
open class Metrics(val started: DateTime) {

    val totalRequests  = AtomicLong(0L)
    val totalFiltered  = AtomicLong(0L)
    val totalErrored   = AtomicLong(0L)
    val totalSucccess  = AtomicLong(0L)
    val lastRequest    = AtomicReference<Job>(Job.empty)
    val lastFiltered   = AtomicReference<Job>(Job.empty)
    val lastErrored    = AtomicReference<Pair<Job, ResultMsg<Any>>>(Pair(Job.empty, ResultFuncs.failure("not started")))
    val lastSuccess    = AtomicReference<Pair<Job, ResultMsg<Any>>>(Pair(Job.empty, ResultFuncs.failure("not started")))


    open fun request(job: Job) {
        totalRequests.incrementAndGet()
        lastRequest.set(job)
    }


    open fun success(job: Job, result:ResultMsg<Any>) {
        totalSucccess.incrementAndGet()
        lastSuccess.set(Pair(job, result))
    }


    open fun filtered(job: Job) {
        totalFiltered.incrementAndGet()
        lastFiltered.set(job)
    }


    open fun errored(job: Job, result:ResultMsg<Any>) {
        totalErrored.incrementAndGet()
        lastErrored.set(Pair(job, result))
    }
}

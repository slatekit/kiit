package slatekit.workers.core

import slatekit.common.DateTime
import slatekit.common.Failure
import slatekit.common.ResultEx
import slatekit.workers.Job
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Metrics track the total and last requests, successes, failures, filtered ( optional )
 * This is currently used for Workers to track totals and last results.
 */
open class Tracker(val started: DateTime) {

    val lastRequest = AtomicReference<Job>(Job.empty)
    val lastFiltered = AtomicReference<Job>(Job.empty)
    val lastFailure = AtomicReference<Pair<Job, ResultEx<*>>>(Pair(Job.empty, Failure(Exception("not started"))))
    val lastInvalid = AtomicReference<Pair<Job, ResultEx<*>>>(Pair(Job.empty, Failure(Exception("not started"))))
    val lastSuccess = AtomicReference<Pair<Job, ResultEx<*>>>(Pair(Job.empty, Failure(Exception("not started"))))

    open fun requested(job: Job) {
        lastRequest.set(job)
    }

    open fun succeeded(job: Job, result: ResultEx<*>) {
        lastSuccess.set(Pair(job, result))
    }

    open fun failed(job: Job, result: ResultEx<*>) {
        lastFailure.set(Pair(job, result))
    }

    open fun invalid(job: Job, result:ResultEx<*>) {
        lastInvalid.set(Pair(job, result))
    }

    open fun filtered(job: Job, result:ResultEx<*>) {
        lastFiltered.set(job)
    }
}

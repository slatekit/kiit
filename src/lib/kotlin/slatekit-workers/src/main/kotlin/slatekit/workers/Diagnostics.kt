package slatekit.workers

import slatekit.common.ResultEx
import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics
import slatekit.common.queues.QueueSource
import slatekit.common.results.*
import slatekit.workers.core.*


/**
 * @param ctx: Application context
 * @param metrics: Metrics to store counters/gauges/meters
 * @param logger: Logger for the API server
 * @param tracker: Tracker to hold the last Request, Response
 * @param events: Event listener for responses
 */
open class Diagnostics(
                  val events: Events,
                  val metrics: Metrics,
                  val logger: Logger,
                  val tracker: Tracker) {

    val REQUEST_TYPE = "Job"
    val METRICS_TYPE = "worker"

    /**
     * Tags for metrics
     */
    val tags = listOf<String>()


    /**
     * Records all types of diagnostics available here ( logs, metrics, tracking, eventing )
     */
    open fun record(sender:Any, queue:QueueSource, worker:Worker<*>, job:Job, result:ResultEx<*>) {
        // Log result of job
        log(sender, queue, job, result)

        // Store metrics of job status counts
        meter(sender, queue, job, result)

        // Track metrics/stats
        track(sender, queue, job, result)

        // Notify the event listener
        notify(sender, queue, worker, job, result)
    }


    /**
     * Logs the result of a processed job
     */
    open fun log(sender: Any, queue: QueueSource, job: Job, result: ResultEx<*>) {
        val info = "id:${job.id}, queue: ${job.queue}, task: ${job.task}, refId: ${job.refId}, result: ${result.code}, msg: ${result.msg}"
        when {
            result.code.isInSuccessRange()    -> logger.info ("$REQUEST_TYPE succeeded: $info")
            result.code.isFilteredOut()       -> logger.info ("$REQUEST_TYPE filtered: $info")
            result.code.isInBadRequestRange() -> logger.error("$REQUEST_TYPE invalid: $info")
            result.code.isInFailureRange()    -> logger.error("$REQUEST_TYPE failed: $info")
            else                              -> logger.error("$REQUEST_TYPE failed: $info")
        }
    }


    /**
     * Tracks the last job for diagnostics
     */
    open fun track(sender: Any, queue: QueueSource, job: Job, result: ResultEx<*>) {
        tracker.requested(job)
        when {
            result.code.isInSuccessRange()    -> tracker.succeeded(job, result)
            result.code.isFilteredOut()       -> tracker.filtered(job, result)
            result.code.isInBadRequestRange() -> tracker.invalid(job, result)
            result.code.isInFailureRange()    -> tracker.failed(job, result)
            else                              -> tracker.failed(job, result)
        }
    }


    /**
     * Records metrics (counts) each job result
     */
    open fun meter(sender: Any, queue: QueueSource, job: Job, result: ResultEx<*>) {
        metrics.count("$METRICS_TYPE.total_requests", tags)
        when {
            result.code.isInSuccessRange()    -> metrics.count("$METRICS_TYPE.total_successes", tags)
            result.code.isFilteredOut()       -> metrics.count("$METRICS_TYPE.total_filtered", tags)
            result.code.isInBadRequestRange() -> metrics.count("$METRICS_TYPE.total_invalid", tags)
            result.code.isInFailureRange()    -> metrics.count("$METRICS_TYPE.total_failed", tags)
            else                              -> metrics.count("$METRICS_TYPE.total_other", tags)
        }
    }


    /**
     * Events out the job result to potential listeners
     */
    open fun notify(sender: Any, queue: QueueSource, worker:Worker<*>, job: Job, result: ResultEx<*>) {
        events.onJobEvent(this, worker, JobRequested)
        when {
            result.code.isInSuccessRange()    -> events.onJobEvent(sender, worker, JobSucceeded)
            result.code.isFilteredOut()       -> events.onJobEvent(sender, worker, JobFiltered)
            result.code.isInBadRequestRange() -> events.onJobEvent(sender, worker, JobInvalid)
            result.code.isInFailureRange()    -> events.onJobEvent(sender, worker, JobFailed)
            else                              -> events.onJobEvent(sender, worker, JobEvent(job.id, result))
        }
    }
}

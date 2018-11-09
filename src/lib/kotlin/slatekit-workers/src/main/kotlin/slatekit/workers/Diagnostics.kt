package slatekit.workers

import slatekit.common.ResultEx
import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics
import slatekit.common.queues.QueueSource
import slatekit.common.results.ResultCode
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
        when (result.code) {
            ResultCode.SUCCESS  -> logger.info("Job ${job.id} succeeded")
            ResultCode.FILTERED -> logger.info("Job ${job.id} filtered")
            ResultCode.FAILURE  -> logger.info("Job ${job.id} failed with ${result.msg}")
            else                -> logger.info("Job ${job.id} failed with code ${result.code}")
        }
    }


    /**
     * Tracks the last job for diagnostics
     */
    open fun track(sender: Any, queue: QueueSource, job: Job, result: ResultEx<*>) {
        tracker.request(job)
        when (result.code) {
            ResultCode.SUCCESS  -> tracker.success(job, result)
            ResultCode.FILTERED -> tracker.filtered(job, result)
            ResultCode.FAILURE  -> tracker.errored(job, result)
            else                -> tracker.errored(job, result)
        }
    }


    /**
     * Records metrics (counts) each job result
     */
    open fun meter(sender: Any, queue: QueueSource, job: Job, result: ResultEx<*>) {
        metrics.count("worker.total_requests", tags)
        when (result.code) {
            ResultCode.SUCCESS  -> metrics.count("worker.total_successes", tags)
            ResultCode.FILTERED -> metrics.count("worker.total_filtered", tags)
            ResultCode.FAILURE  -> metrics.count("worker.total_failed", tags)
            else                -> metrics.count("worker.total_other", tags)
        }
    }


    /**
     * Events out the job result to potential listeners
     */
    open fun notify(sender: Any, queue: QueueSource, worker:Worker<*>, job: Job, result: ResultEx<*>) {
        events.onJobEvent(this, worker, JobRequested)
        when (result.code) {
            ResultCode.SUCCESS  -> events.onJobEvent(sender, worker, JobSucceeded)
            ResultCode.FILTERED -> events.onJobEvent(sender, worker, JobFiltered)
            ResultCode.FAILURE  -> events.onJobEvent(sender, worker, JobFailed)
            else                -> events.onJobEvent(sender, worker, JobEvent(job.id, result))
        }
    }
}

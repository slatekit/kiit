package slatekit.server.common

import slatekit.apis.core.Events
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.Response
import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics
import slatekit.common.results.ResultChecks
import slatekit.common.results.ResultCode
import slatekit.common.utils.Tracker

/**
 * @param ctx: Application context
 * @param metrics: Metrics to store counters/gauges/meters
 * @param logger: Logger for the API server
 * @param tracker: Tracker to hold the last Request, Response
 * @param events: Event listener for responses
 */
class Diagnostics(val ctx: Context,
                  val events: Events,
                  val metrics:Metrics,
                  val logger:Logger,
                  val tracker: Tracker<Request, Request, Response<*>, Exception>) {

    /**
     * Placeholder for an empty exception
     */
    val error = Exception("n/a")


    /**
     * Record all relevant diagnostics
     */
    fun record(sender:Any, request:Request, result: Response<*>) {
        // Log results
        log(sender, request, result)

        // Track the last results for diagnostics
        track(sender, request, result)

        // Update metrics
        meter(sender, request, result)

        // Notify potential listeners
        notify(sender, request, result)
    }


    /**
     * Logs the result of a processed job
     */
    fun log(sender: Any, request: Request, result: Response<*>) {
        val info = "${request.path}, tag: ${request.tag}, result: ${result.code}, msg: ${result.msg}"
        when {
            ResultChecks.isSuccessRange(result.code) -> logger.info("Request succeeded: $info")
            ResultChecks.isFilteredOut(result.code) -> logger.info("Request filtered:  $info")
            ResultChecks.isBadRequestRange(result.code) -> logger.error("Request failed: $info", result.err ?: error)
            else -> logger.error("Request failed: $info", result.err ?: error)
        }
    }


    /**
     * Tracks the last job for diagnostics
     */
    fun track(sender: Any, request: Request, result: Response<*>) {
        tracker.requested(request)
        when (result.code) {
            ResultCode.SUCCESS -> tracker.succeeded(result)
            ResultCode.FILTERED -> tracker.filtered(request)
            ResultCode.FAILURE -> tracker.failed(request, result.err ?: error)
            else -> tracker.failed(request, result.err ?: error)
        }
    }


    /**
     * Records metrics (counts) each job result
     */
    fun meter(sender: Any, request: Request, result: Response<*>) {
        val tags = listOf("uri", request.path)
        metrics.count("apis.requests.${result.code}", tags)
        metrics.count("apis.total_requests", tags)
        when (result.code) {
            ResultCode.SUCCESS -> metrics.count("apis.total_successes", tags)
            ResultCode.FILTERED -> metrics.count("apis.total_filtered", tags)
            ResultCode.FAILURE -> metrics.count("apis.total_failed", tags)
            else -> metrics.count("apis.total_other", tags)
        }
    }


    /**
     * Events out the job result to potential listeners
     */
    fun notify(sender: Any, request: Request, result: Response<*>) {
        events.onReqest(sender, request)
        when (result.code) {
            ResultCode.SUCCESS -> events.onSuccess(sender, request, result)
            ResultCode.FILTERED -> events.onFiltered(sender, request, result)
            ResultCode.FAILURE -> events.onErrored(sender, request, result)
            else -> events.onEvent(sender, request, result)
        }
    }
}
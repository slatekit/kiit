package slatekit.server.common

import slatekit.apis.core.Events
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.Response
import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics
import slatekit.common.results.*
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
                  val metrics: Metrics,
                  val logger: Logger,
                  val tracker: Tracker<Request, Request, Response<*>, Exception>) {

    val REQUEST_TYPE = "Request"
    val METRICS_TYPE = "api"

    /**
     * Placeholder for an empty exception
     */
    val error = Exception("n/a")


    /**
     * Record all relevant diagnostics
     */
    fun record(sender: Any, request: Request, result: Response<*>) {
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
            result.code.isInSuccessRange()    -> logger.info ("$REQUEST_TYPE succeeded: $info")
            result.code.isFilteredOut()       -> logger.info ("$REQUEST_TYPE filtered:  $info")
            result.code.isInBadRequestRange() -> logger.error("$REQUEST_TYPE invalid: $info", result.err ?: error)
            result.code.isInFailureRange()    -> logger.error("$REQUEST_TYPE failed: $info", result.err ?: error)
            else                              -> logger.error("$REQUEST_TYPE failed: $info", result.err ?: error)
        }
    }


    /**
     * Tracks the last job for diagnostics
     */
    fun track(sender: Any, request: Request, result: Response<*>) {
        tracker.requested(request)
        when  {
            result.code.isInSuccessRange()    -> tracker.succeeded(result)
            result.code.isFilteredOut()       -> tracker.filtered(request)
            result.code.isInBadRequestRange() -> tracker.invalid(request, result.err ?: error)
            result.code.isInFailureRange()    -> tracker.failed(request, result.err ?: error)
            else                              -> tracker.failed(request, result.err ?: error)
        }
    }


    /**
     * Records metrics (counts) each job result
     */
    fun meter(sender: Any, request: Request, result: Response<*>) {
        val tags = listOf("uri", request.path)
        metrics.count("$METRICS_TYPE.requests.${result.code}", tags)
        metrics.count("$METRICS_TYPE.total_requests", tags)
        when  {
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
    fun notify(sender: Any, request: Request, result: Response<*>) {
        events.onReqest(sender, request)
        when {
            result.code.isInSuccessRange()    -> events.onSuccess(sender, request, result)
            result.code.isFilteredOut()       -> events.onFiltered(sender, request, result)
            result.code.isInBadRequestRange() -> events.onInvalid(sender, request, result)
            result.code.isInFailureRange()    -> events.onErrored(sender, request, result)
            else                              -> events.onEvent(sender, request, result)
        }
    }
}
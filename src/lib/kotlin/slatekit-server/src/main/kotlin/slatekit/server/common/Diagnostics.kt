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
                  val metrics: Metrics,
                  val logger: Logger,
                  val tracker: Tracker<Request, Request, Response<*>, Exception>) {

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
            ResultChecks.isSuccessRange(result.code)    -> logger.info("Request succeeded: $info")
            ResultChecks.isFilteredOut(result.code)     -> logger.info("Request filtered:  $info")
            ResultChecks.isBadRequestRange(result.code) -> logger.error("Request invalid: $info", result.err ?: error)
            ResultChecks.isFailureRange(result.code)    -> logger.error("Request failed: $info", result.err ?: error)
            else -> logger.error("Request failed: $info", result.err ?: error)
        }
    }


    /**
     * Tracks the last job for diagnostics
     */
    fun track(sender: Any, request: Request, result: Response<*>) {
        tracker.requested(request)
        when  {
            ResultChecks.isSuccessRange(result.code)    -> tracker.succeeded(result)
            ResultChecks.isFilteredOut(result.code)     -> tracker.filtered(request)
            ResultChecks.isBadRequestRange(result.code) -> tracker.invalid(request, result.err ?: error)
            ResultChecks.isFailureRange(result.code)    -> tracker.failed(request, result.err ?: error)
            else -> tracker.failed(request, result.err ?: error)
        }
    }


    /**
     * Records metrics (counts) each job result
     */
    fun meter(sender: Any, request: Request, result: Response<*>) {
        val tags = listOf("uri", request.path)
        metrics.count("api.requests.${result.code}", tags)
        metrics.count("api.total_requests", tags)
        when  {
            ResultChecks.isSuccessRange(result.code)    -> metrics.count("api.total_successes", tags)
            ResultChecks.isFilteredOut(result.code)     -> metrics.count("api.total_filtered", tags)
            ResultChecks.isBadRequestRange(result.code) -> metrics.count("api.total_invalid", tags)
            ResultChecks.isFailureRange(result.code)    -> metrics.count("api.total_failed", tags)
            else -> metrics.count("api.total_other", tags)
        }
    }


    /**
     * Events out the job result to potential listeners
     */
    fun notify(sender: Any, request: Request, result: Response<*>) {
        events.onReqest(sender, request)
        when {
            ResultChecks.isSuccessRange(result.code)    -> events.onSuccess(sender, request, result)
            ResultChecks.isFilteredOut(result.code)     -> events.onFiltered(sender, request, result)
            ResultChecks.isBadRequestRange(result.code) -> events.onInvalid(sender, request, result)
            ResultChecks.isFailureRange(result.code)    -> events.onErrored(sender, request, result)
            else -> events.onEvent(sender, request, result)
        }
    }
}
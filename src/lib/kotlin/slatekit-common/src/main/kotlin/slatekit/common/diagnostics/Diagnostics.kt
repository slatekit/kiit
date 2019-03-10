package slatekit.common.diagnostics

import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics
import slatekit.common.requests.*

/**
 * Handles boilerplate diagnostics which include ( logs, metrics, tracking ( last request/result ) and eventing
 * @param ctx: Application context
 * @param metrics: Metrics to store counters/gauges/meters
 * @param logger: Logger for the API server
 * @param tracker: Tracker to hold the last Request, Response
 * @param events: Event listener for responses
 */
open class Diagnostics<TRequest>(
        val prefix:String,
        val nameFetcher:(TRequest) -> String,
        val infoFetcher:(TRequest) -> String,
        val metricFetcher:(TRequest) -> String,
        val tagsFetcher:(TRequest) -> List<String>,
        val logger: Logger? = null,
        val metrics: Metrics? = null,
        val events: Events<TRequest, Response<*>, Exception>? = null,
        val tracker: Tracker<TRequest, Response<*>, Exception>? = null) {


    /**
     * Record all relevant diagnostics
     */
    open fun record(sender: Any, request: TRequest, result: Response<*>) {
        // Log results
        logger?.let { log(sender, request, result) }

        // Track the last response
        tracker?.let { track(sender, request, result) }

        // Update metrics
        metrics?.let { meter(sender, request, result) }

        // Notify event listeners
        events?.let { notify(sender, request, result) }
    }


    /**
     * Logs the response
     */
    open fun log(sender: Any, request:TRequest, response: Response<*>) {
        logger?.let {
            val name = nameFetcher(request)
            val info = infoFetcher(request)
            val more = "result: ${response.code}, msg: ${response.msg}"

            when {
                response.isInSuccessRange()    -> logger.info ("$prefix $name succeeded: $info $more")
                response.isFilteredOut()       -> logger.info ("$prefix $name filtered: $info $more")
                response.isInBadRequestRange() -> logger.error("$prefix $name invalid: $info $more")
                response.isInFailureRange()    -> logger.error("$prefix $name failed: $info $more")
                else                           -> logger.error("$prefix $name failed: $info $more")
            }
        }
    }


    /**
     * Tracks the last request/response/error
     */
    open fun track(sender: Any, request:TRequest, response: Response<*>) {
        tracker?.let {
            tracker.requested(request)
            when {
                response.isInSuccessRange()    -> tracker.succeeded(request, response)
                response.isFilteredOut()       -> tracker.filtered(request)
                response.isInBadRequestRange() -> tracker.invalid(request, response.err)
                response.isInFailureRange()    -> tracker.failed(request, response.err)
                else                           -> tracker.failed(request, response.err)
            }
        }
    }


    /**
     * Records metrics (counts) for each request/response/error
     */
    open fun meter(sender: Any, request:TRequest, response: Response<*>) {
        metrics?.let {
            val metric = metricFetcher(request)
            val tags = tagsFetcher(request)
            metrics.count("$metric.total_requests", tags)
            when {
                response.isInSuccessRange()    -> metrics.count("$metric.total_successes", tags)
                response.isFilteredOut()       -> metrics.count("$metric.total_filtered", tags)
                response.isInBadRequestRange() -> metrics.count("$metric.total_invalid", tags)
                response.isInFailureRange()    -> metrics.count("$metric.total_failed", tags)
                else                           -> metrics.count("$metric.total_other", tags)
            }
        }
    }


    /**
     * Events out request/response/error
     */
    fun notify(sender: Any, request: TRequest, response: Response<*>) {
        events?.let {
            events.onRequest(sender, request)
            when {
                response.isInSuccessRange()    -> events.onSuccess(sender, request, response)
                response.isFilteredOut()       -> events.onFiltered(sender, request, response)
                response.isInBadRequestRange() -> events.onInvalid(sender, request, response.err)
                response.isInFailureRange()    -> events.onErrored(sender, request, response.err)
                else                           -> events.onEvent(sender, request, response)
            }
        }
    }
}
package slatekit.server.core

import slatekit.telemetry.Diagnostics
import slatekit.common.log.Logger
import slatekit.telemetry.Metrics
import slatekit.requests.*

/**
 * Standardized diagnostics using the Diagnostics component from common.
 * Provides built-in support for :
 * 1. logs
 * 2. metrics
 * 3. tracker ( last request/response )
 * 4. events ( event listneners )
 * @param metrics: Metrics to store counters/gauges/meters
 * @param logger: Logger for the API server
 */
class ServerDiagnostics(override val source: String,
                        override val logger: Logger?,
                        override val metrics: Metrics?,
                        val tags:List<String>) : Diagnostics<Request> {
    /**
     * Record all relevant diagnostics
     */
    override fun record(sender: Any, request: Request, response: Response<*>, target:Array<String>?) {
        //logger?.let  { log(sender, request, response) }
        metrics?.let { meter(sender, request, response) }
    }


    /**
     * Record all relevant diagnostics
     */
    private fun log(sender: Any, request: Request, response: Response<*>) {
        val msg = response.desc ?: ""
        when {
            response.isInSuccessRange()    -> logger?.info ("$source succeeded: $msg")
            response.isFilteredOut()       -> logger?.info ("$source filtered : $msg")
            response.isInBadRequestRange() -> logger?.error("$source invalid  : $msg")
            response.isInFailureRange()    -> logger?.error("$source failed   : $msg")
            else                           -> logger?.error("$source failed   : $msg")
        }
    }


    /**
     * Record all relevant diagnostics
     */
    private fun meter(sender: Any, request: Request, response: Response<*>) {
        val msg = response.desc ?: ""
        when {
            response.isInSuccessRange()    -> metrics?.count("$source.total_successes", tags)
            response.isFilteredOut()       -> metrics?.count("$source.total_filtered", tags)
            response.isInBadRequestRange() -> metrics?.count("$source.total_invalid", tags)
            response.isInFailureRange()    -> metrics?.count("$source.total_failed", tags)
            else                           -> metrics?.count("$source.total_other", tags)
        }
    }
}
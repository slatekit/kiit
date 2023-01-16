package kiit.telemetry

import kiit.common.log.Logger
import kiit.requests.*

/**
 * Interface for diagnostics for core components
 */
interface Diagnostics<TRequest> {
        val source:String
        val logger: Logger?
        val metrics: Metrics?


    /**
     * Record all relevant diagnostics
     */
    fun record(sender: Any, request: TRequest, response: Response<*>, target:Array<String>? = null)
}

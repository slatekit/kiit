package slatekit.common

import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics
import slatekit.common.requests.*

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
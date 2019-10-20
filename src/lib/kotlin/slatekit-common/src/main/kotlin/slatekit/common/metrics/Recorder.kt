package slatekit.common.metrics

import slatekit.common.Identity
import slatekit.common.log.Logger
import slatekit.common.log.LoggerConsole
import slatekit.results.Err
import slatekit.results.Outcome
import slatekit.results.builders.Tries

/**
 * Used for diagnostics / metrics to record a request/response/failure for some function/operation
 * identified by @param id,
 * This serves to record the operation across all relevant diagnostic services.
 *
 * 1. logs  : via structured logging by converting request/response/failure to an event
 * 2. lasts : storing of last request/results e.g. scheduled tasks, syncs, commands
 * 3. counts: counting the various statuses ( succeeded, denied, ignored, invalid, etc
 * 4. events: calling any custom event handlers
 */
open class Recorder<TRequest, TResponse>(val id: Identity,
                                         val logger: Logger,
                                         val calls: Calls,
                                         val counts: Counters,
                                         val lasts: Lasts<TRequest, TResponse, Err>?,
                                         val converter: ((TRequest, Outcome<TResponse>) -> Event)?,
                                         val events: Events<TRequest, TResponse, Err>?) {

    /**
     * Record all relevant diagnostics
     */
    fun record(sender: Any, request: TRequest, result: Outcome<TResponse>) {
        Tries.attempt {
            // Structured logging ( convert the request/result into an Event
            logger?.let { converter?.let { c -> Event.log(logger, id, c(request, result)) } }

            // Track the last response
            lasts?.let { lasts.handle(sender, request, result) }

            // Update metrics
            counts?.let { Counters.count(counts, result.status) }

            // Notify event listeners
            events?.let { events.handle(sender, request, result) }
        }
    }


    companion object {

        fun <TRequest, TResponse> of(id: Identity,
                                     tags:List<Tag>? = null,
                                     logger: Logger = LoggerConsole(),
                                     converter: ((TRequest, Outcome<TResponse>) -> Event)? = null): Recorder<TRequest, TResponse> {
            val events = when(converter) {
                null -> null
                else -> Events<TRequest, TResponse, Err>(tags ?: listOf())
            }
            return Recorder(id, logger, Calls(id), Counters(id), Lasts(id), converter, events)
        }
    }
}
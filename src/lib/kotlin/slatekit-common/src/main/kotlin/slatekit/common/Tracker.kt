/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.common

import slatekit.common.log.Logger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

open class Tracker<TRequest, TFilter, TSuccess, TFailure>(val id: String, val name: String, val logger: Logger) {

    // Totals
    val totalRequests = AtomicLong(0L)
    val totalFiltered = AtomicLong(0L)
    val totalSuccesses = AtomicLong(0L)
    val totalFailures = AtomicLong(0L)

    // Last values
    val lastRequest = AtomicReference<TRequest>(null)
    val lastFiltered = AtomicReference<TFilter>(null)
    val lastSuccess = AtomicReference<TSuccess>(null)
    val lastFailure = AtomicReference<Pair<TRequest, TFailure>>(null)

    // Status
    val currentStatus = AtomicReference<String>("n/a")


    /**
     * Logs basic diagnostics
     */
    fun log() {
        diagnostics().forEach({ metric -> logger?.info ("$name ${metric.first} : ${metric.second}", null)})
    }


    /**
     * Gets basic diagnostics about this consumer
     */
    fun diagnostics(): List<Pair<String, String>> {
        val requests = totalRequests.get().toDouble()
        val filtered = totalFiltered.get().toDouble()
        val successes = totalSuccesses.get().toDouble()
        val failures = totalFailures.get().toDouble()
        fun percentage(a:Double, b:Double): Double {
            return if( b == 0.0 ) 0.0 else a / b
        }
        val percentFiltered = percentage(filtered , requests )
        val percentSuccess  = percentage(successes, requests )
        val percentFailure  = percentage(failures , requests )

        return listOf(
            Pair("id", id),
            Pair("name", name),
            Pair("status", currentStatus.get()),
            Pair("date", DateTime.now().toString()),
            Pair("total requests"   , requests.toString()),
            Pair("total filtered"   , filtered.toString()),
            Pair("total successes"  , successes.toString()),
            Pair("total failures"   , failures.toString()),
            Pair("percent filtered" , percentFiltered.toString()),
            Pair("percent successes", percentSuccess.toString()),
            Pair("percent failures" , percentFailure.toString()),
            Pair("last  request"    , lastRequest.get()?.toString() ?: ""),
            Pair("last  filtered"   , lastFiltered.get()?.toString() ?: ""),
            Pair("last  success"    , lastSuccess.get()?.toString() ?: ""),
            Pair("last  failure"    , lastFailure.get()?.toString() ?: "")
        )
    }


    /**
     * Handle response from crediting
     * @param result
     */
    fun handleResponse(request: TRequest, result: Result<TSuccess, TFailure>) {
        result.onSuccess {
            trackSuccess(it)
            logger.info("Success: " + it.toString(), null)
        }
        result.onFailure {
            trackFailure(request, it)
            logger.error("Failure: " + it.toString(), null)
        }
    }


    /**
     * Handle failure ( e.g. unexpected failure ) vs an expected failured which
     * will be handled in the handleResponse method above
     */
    open fun handleFailure(request: TRequest, err: TFailure) {
        when (err) {
            is Exception -> logger.error("Failure :${err.message}", err)
            else -> logger.error("Failure : $err", null)
        }
    }


    /**
     * Keep track of total requests ( regardless of success/error )
     */
    open fun trackRequest(request: TRequest) {
        lastRequest.set(request)
        totalRequests.incrementAndGet()
    }


    /**
     * Keep track of total filtered requests ( regardless of success/error )
     */
    open fun trackFiltered(filteredReason: TFilter) {
        lastFiltered.set(filteredReason)
        totalFiltered.incrementAndGet()
    }


    /**
     * Keep track of total successfully processed engagements
     */
    open fun trackSuccess(success: TSuccess) {
        lastSuccess.set(success)
        totalSuccesses.incrementAndGet()
    }


    /**
     * Keep track of total errors
     * @param err
     */
    open fun trackFailure(req:TRequest, failure: TFailure) {
        lastFailure.set(Pair(req, failure))
        totalFailures.incrementAndGet()
    }
}

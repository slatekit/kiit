package slatekit.telemetry

import slatekit.common.Identity
import slatekit.results.Failed
import slatekit.results.Passed
import slatekit.results.Status
import java.util.concurrent.atomic.AtomicLong


/**
 * Used for diagnostics / metrics to track and count various @see[slatekit.results.Status]
 * representing successes / failures of some operation identified by @param id.
 * This serves to track the following:
 *
 * 1. total requests  ( processed )
 * 2. total successes  e.g. passed
 * 3. total denied     e.g. security/auth failure
 * 4. total invalid    e.g. invalid / bad request
 * 5. total ignored    e.g. ineligible request
 * 6. total errored    e.g. expected errors
 * 7. total unexpected e.g. unexpected errors
 */
class Counters(val id: Identity = Identity.empty,
               val tags:List<Tag> = listOf(),
               custom:List<String>? = null) {


    private val counters = build(custom)
    val processed = Counter()
    val succeeded = Counter()
    val denied    = Counter()
    val invalid   = Counter()
    val ignored   = Counter()
    val errored   = Counter()
    val unknown   = Counter()

    /**
     * Track status in the counters
     */
    fun increment(status:Status) {
        processed.inc()
        when(status) {
            is Passed.Succeeded  -> succeeded.inc()
            is Failed.Denied     -> denied.inc()
            is Failed.Invalid    -> invalid.inc()
            is Failed.Ignored    -> ignored.inc()
            is Failed.Errored    -> errored.inc()
            is Failed.Unknown    -> unknown.inc()
            else                 -> unknown.inc()
        }
    }

    fun get(name:String):Long = getInternal(name)?.get() ?: 0L
    fun inc(name:String):Long = getInternal(name)?.inc() ?: 0L
    fun dec(name:String):Long = getInternal(name)?.dec() ?: 0L


    /**
     * Reset all counterst to 0
     */
    fun reset() {
        processed.set(0L)
        succeeded.set(0L)
        denied.set(0L)
        invalid.set(0L)
        ignored.set(0L)
        errored.set(0L)
        unknown.set(0L)
        counters.keys.map { counters[it]?.set(0L) }
    }


    /**
     * Gets the counter for a specific name
     */
    private fun getInternal(name:String): Counter? {
        return if(counters.containsKey(name)) {
            counters[name]
        } else {
            null
        }
    }


    companion object {
        val PROCESSED  = "Processed"
        val SUCCEEDED  = "Succeeded"
        val DENIED     = "Denied"
        val INVALID    = "Invalid"
        val IGNORED    = "Ignored"
        val ERRORED    = "Errored"
        val UNEXPECTED = "Unexpected"

        /**
         * Track status in the counters
         */
        fun count(counters: Counters, status:Status) {
            counters.increment(status)
        }


        /**
         * Tracks the status of all the results using the counters
         */
        fun <S,F> count(counters: Counters, results:List<slatekit.results.Result<S, F>>): Unit {
            // 1. Track processed, failed, etc
            results.forEach { result -> counters.increment(result.status) }
        }


        /**
         * Count the number of processed items
         * @param counter   :
         * @param limit
         * @param operation
         */
        fun countOrReset(counter:AtomicLong, enabled:Boolean, limit:Long, size:Int, operation:() -> Unit) {
            val current = counter.get()
            if (current >= limit) {
                if(enabled) {
                    operation()
                }
                counter.set(0L)
            }
            else {
                counter.set(current + size)
            }
        }


        fun build(custom:List<String>? = null):Map<String, Counter> {
            return custom?.let { names -> names.map { it to Counter(listOf()) }.toMap() } ?: mapOf()
        }
    }
}

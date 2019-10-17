package slatekit.common.metrics

import slatekit.common.Identity
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
class Counters(val id: Identity,
               override val tags:List<Tag> = listOf(),
               lookup:Map<String, Counter>? = null,
               custom:List<String>? = null) : Countable {

    private val counters = build(lookup, custom)


    override fun get(name:String):Long = getInternal(name)?.get() ?: 0L
    override fun inc(name:String):Long = getInternal(name)?.inc() ?: 0L
    override fun dec(name:String):Long = getInternal(name)?.dec() ?: 0L


    /**
     * Reset all counterst to 0
     */
    override fun reset() {
        getInternal(Countable.PROCESSED )   ?.set(0L)
        getInternal(Countable.SUCCEEDED )   ?.set(0L)
        getInternal(Countable.DENIED    )   ?.set(0L)
        getInternal(Countable.INVALID   )   ?.set(0L)
        getInternal(Countable.IGNORED   )   ?.set(0L)
        getInternal(Countable.ERRORED   )   ?.set(0L)
        getInternal(Countable.UNEXPECTED)   ?.set(0L)
    }


    /**
     * Gets the counter for a specific name
     */
    private fun getInternal(name:String):Counter? {
        return if(counters.containsKey(name)) {
            counters[name]
        } else {
            null
        }
    }


    companion object {

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


        fun build(lookup:Map<String, Counter>? = null, custom:List<String>? = null):Map<String, Counter> {
            val initial = lookup ?: listOf(
                    Countable.PROCESSED ,
                    Countable.SUCCEEDED ,
                    Countable.DENIED    ,
                    Countable.INVALID   ,
                    Countable.IGNORED   ,
                    Countable.ERRORED   ,
                    Countable.UNEXPECTED
            ).map{ it to Counter(listOf()) }.toMap()
            val all = custom?.let {
                val pairs = it.map { it to Counter(listOf()) }
                initial.plus(pairs)
            } ?: initial
            return all
        }
    }
}
package slatekit.common.metrics

import slatekit.results.Status

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
interface Countable : Tagged {

    /**
     * Increment the counters for the different states
     * @return
     */
    fun processed():Long    = inc("processed")
    fun succeeded():Long    = inc(Status::Succeeded.name)
    fun denied():Long       = inc(Status::Denied.name)
    fun invalid():Long      = inc(Status::Invalid.name)
    fun ignored():Long      = inc(Status::Ignored.name)
    fun errored():Long      = inc(Status::Errored.name)
    fun unexpected():Long   = inc(Status::Unexpected.name)
    fun custom(name:String) = inc(name)


    fun totalProcessed ():Long = get("processed")
    fun totalSucceeded ():Long = get(Status::Succeeded.name)
    fun totalInvalid   ():Long = get(Status::Denied.name)
    fun totalIgnored   ():Long = get(Status::Invalid.name)
    fun totalDenied    ():Long = get(Status::Ignored.name)
    fun totalErrored   ():Long = get(Status::Errored.name)
    fun totalUnexpected():Long = get(Status::Unexpected.name)
    fun totalCustom(name:String):Long = get(name)


    fun reset()
    fun get(name:String):Long
    fun inc(name:String):Long
    fun dec(name:String):Long


    /**
     * Track status in the counters
     */
    fun count(status:Status) {
        processed()
        when(status) {
            is Status.Denied     -> denied()
            is Status.Invalid    -> invalid()
            is Status.Ignored    -> ignored()
            is Status.Errored    -> errored()
            is Status.Unexpected -> unexpected()
            else                 -> unexpected()
        }
    }
}
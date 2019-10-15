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
    fun incProcessed():Long    = inc("processed")
    fun incSucceeded():Long    = inc(Status::Succeeded.name)
    fun incDenied():Long       = inc(Status::Denied.name)
    fun incInvalid():Long      = inc(Status::Invalid.name)
    fun incIgnored():Long      = inc(Status::Ignored.name)
    fun incErrored():Long      = inc(Status::Errored.name)
    fun incUnexpected():Long   = inc(Status::Unexpected.name)
    fun incCustom(name:String) = inc(name)


    /**
     * Decrement the counters for the different states
     * @return
     */
    fun decProcessed():Long    = dec("processed")
    fun decSucceeded():Long    = dec(Status::Succeeded.name)
    fun decDenied():Long       = dec(Status::Denied.name)
    fun decInvalid():Long      = dec(Status::Invalid.name)
    fun decIgnored():Long      = dec(Status::Ignored.name)
    fun decErrored():Long      = dec(Status::Errored.name)
    fun decUnexpected():Long   = dec(Status::Unexpected.name)
    fun decCustom(name:String) = dec(name)


    /**
     * Gets the current values for the different counters
     */
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
    fun increment(status:Status) {
        incProcessed()
        when(status) {
            is Status.Succeeded  -> incSucceeded()
            is Status.Denied     -> incDenied()
            is Status.Invalid    -> incInvalid()
            is Status.Ignored    -> incIgnored()
            is Status.Errored    -> incErrored()
            is Status.Unexpected -> incUnexpected()
            else                 -> incUnexpected()
        }
    }
}
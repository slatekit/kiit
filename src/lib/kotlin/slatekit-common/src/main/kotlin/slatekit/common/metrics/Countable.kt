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
    fun incProcessed():Long    = inc(Countable.PROCESSED )
    fun incSucceeded():Long    = inc(Countable.SUCCEEDED )
    fun incDenied():Long       = inc(Countable.DENIED    )
    fun incInvalid():Long      = inc(Countable.INVALID   )
    fun incIgnored():Long      = inc(Countable.IGNORED   )
    fun incErrored():Long      = inc(Countable.ERRORED   )
    fun incUnexpected():Long   = inc(Countable.UNEXPECTED)
    fun incCustom(name:String) = inc(name)


    /**
     * Decrement the counters for the different states
     * @return
     */
    fun decProcessed():Long    = dec(Countable.PROCESSED )
    fun decSucceeded():Long    = dec(Countable.SUCCEEDED )
    fun decDenied():Long       = dec(Countable.DENIED    )
    fun decInvalid():Long      = dec(Countable.INVALID   )
    fun decIgnored():Long      = dec(Countable.IGNORED   )
    fun decErrored():Long      = dec(Countable.ERRORED   )
    fun decUnexpected():Long   = dec(Countable.UNEXPECTED)
    fun decCustom(name:String) = dec(name)


    /**
     * Gets the current values for the different counters
     */
    fun totalProcessed ():Long = get(Countable.PROCESSED )
    fun totalSucceeded ():Long = get(Countable.SUCCEEDED )
    fun totalDenied    ():Long = get(Countable.DENIED    )
    fun totalInvalid   ():Long = get(Countable.INVALID   )
    fun totalIgnored   ():Long = get(Countable.IGNORED   )
    fun totalErrored   ():Long = get(Countable.ERRORED   )
    fun totalUnexpected():Long = get(Countable.UNEXPECTED)
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


    companion object {
        val PROCESSED  = "Processed"
        val SUCCEEDED  = "Succeeded"
        val DENIED     = "Denied"
        val INVALID    = "Invalid"
        val IGNORED    = "Ignored"
        val ERRORED    = "Errored"
        val UNEXPECTED = "Unexpected"
    }
}
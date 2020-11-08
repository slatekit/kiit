package slatekit.tracking

import slatekit.results.Failed
import slatekit.results.Passed
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
    fun incProcessed():Long    = inc(PROCESSED)
    fun incSucceeded():Long    = inc(SUCCEEDED)
    fun incDenied():Long       = inc(DENIED)
    fun incInvalid():Long      = inc(INVALID)
    fun incIgnored():Long      = inc(IGNORED)
    fun incErrored():Long      = inc(ERRORED)
    fun incUnexpected():Long   = inc(UNEXPECTED)
    fun incCustom(name:String) = inc(name)


    /**
     * Decrement the counters for the different states
     * @return
     */
    fun decProcessed():Long    = dec(PROCESSED)
    fun decSucceeded():Long    = dec(SUCCEEDED)
    fun decDenied():Long       = dec(DENIED)
    fun decInvalid():Long      = dec(INVALID)
    fun decIgnored():Long      = dec(IGNORED)
    fun decErrored():Long      = dec(ERRORED)
    fun decUnexpected():Long   = dec(UNEXPECTED)
    fun decCustom(name:String) = dec(name)


    /**
     * Gets the current values for the different counters
     */
    fun totalProcessed ():Long = get(PROCESSED)
    fun totalSucceeded ():Long = get(SUCCEEDED)
    fun totalDenied    ():Long = get(DENIED)
    fun totalInvalid   ():Long = get(INVALID)
    fun totalIgnored   ():Long = get(IGNORED)
    fun totalErrored   ():Long = get(ERRORED)
    fun totalUnexpected():Long = get(UNEXPECTED)
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
            is Passed.Succeeded  -> incSucceeded()
            is Failed.Denied     -> incDenied()
            is Failed.Invalid    -> incInvalid()
            is Failed.Ignored    -> incIgnored()
            is Failed.Errored    -> incErrored()
            is Failed.Unknown    -> incUnexpected()
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

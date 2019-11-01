package slatekit.tracking

import slatekit.common.DateTime
import slatekit.common.Identity
import slatekit.results.Err
import java.util.concurrent.atomic.AtomicReference

/**
 * Used for diagnostics / metrics to track calls made to some function/target identified by @param id
 * This serves to track the following:
 *
 * 1. total calls made
 * 2. total calls passed
 * 3. total calls failed
 * 4. last error
 * 5. last time of call
 */
class Calls(val id: Identity) {
    private val counters = Counters(id)
    private val lastErr = AtomicReference<Err>()
    private val lastRunTime = AtomicReference<DateTime>()


    fun hasRun():Boolean = totalRuns() > 0


    fun inc(): Long {
        lastRunTime.set(DateTime.now())
        return counters.incProcessed()
    }


    fun passed(): Long = counters.incSucceeded()


    fun failed(): Long = counters.incErrored()


    fun failed(ex:Exception): Long {
        lastErr.set(Err.of(ex))
        return counters.incUnexpected()
    }


    fun totalRuns():Long = counters.totalProcessed()
    fun totalPassed():Long = counters.totalSucceeded()
    fun totalFailed():Long = counters.totalErrored() + counters.totalUnexpected()
    fun lastError():Err? = lastErr.get()
    fun lastTime():DateTime? = lastRunTime.get()
}
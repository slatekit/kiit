package kiit.telemetry

import kiit.common.DateTime
import kiit.common.Identity
import kiit.results.Err
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
        return counters.processed.inc()
    }


    fun passed(): Long = counters.succeeded.inc()


    fun failed(): Long = counters.errored.inc()


    fun failed(ex:Exception): Long {
        lastErr.set(Err.ex(ex))
        return counters.unknown.inc()
    }


    fun totalRuns():Long = counters.processed.get()
    fun totalPassed():Long = counters.succeeded.get()
    fun totalFailed():Long = counters.errored.get() + counters.unknown.get()
    fun lastError():Err? = lastErr.get()
    fun lastTime():DateTime? = lastRunTime.get()
}

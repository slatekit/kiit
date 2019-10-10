package slatekit.common.metrics

import slatekit.common.DateTime
import slatekit.common.ids.Identity
import slatekit.results.Err
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class Calls(val id:Identity) {

    private val runsCounter = AtomicLong(0L)
    private val passedCounter = AtomicLong(0L)
    private val failedCounter = AtomicLong(0L)
    private val lastErr = AtomicReference<Err>()
    private val lastRunTime = AtomicReference<DateTime>()


    fun hasRun():Boolean = totalRuns() > 0

    fun inc(): Long {
        lastRunTime.set(DateTime.now())
        return runsCounter.incrementAndGet()
    }

    fun passed(): Long = passedCounter.incrementAndGet()

    fun failed(): Long = failedCounter.incrementAndGet()

    fun failed(ex:Exception): Long {
        lastErr.set(Err.of(ex))
        return failedCounter.incrementAndGet()
    }

    fun totalRuns():Long = runsCounter.get()
    fun totalPassed():Long = passedCounter.get()
    fun totalFailed():Long = failedCounter.get()
    fun lastError():Err? = lastErr.get()
    fun lastTime():DateTime? = lastRunTime.get()
}
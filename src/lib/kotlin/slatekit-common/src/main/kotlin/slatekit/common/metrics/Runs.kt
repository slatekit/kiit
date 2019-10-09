package slatekit.common.metrics

import slatekit.common.DateTime
import slatekit.results.Err
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class Runs {

    val runsCounter = AtomicLong(0L)
    val passedCounter = AtomicLong(0L)
    val failedCounter = AtomicLong(0L)
    val time = AtomicReference<DateTime>()

    val lastError = AtomicReference<Err>()
    val lastRunTime = AtomicReference<DateTime>()


    val hasRun: Boolean get() = this.runsCounter.get() > 0


    fun run(): Long { time.set(DateTime.now()); return runsCounter.incrementAndGet(); }
    fun passed(): Long = passedCounter.incrementAndGet()
    fun failed(): Long = failedCounter.incrementAndGet()


    fun totalRuns():Long = runsCounter.get()
    fun totalPassed():Long = passedCounter.get()
    fun totalFailed():Long = failedCounter.get()
}
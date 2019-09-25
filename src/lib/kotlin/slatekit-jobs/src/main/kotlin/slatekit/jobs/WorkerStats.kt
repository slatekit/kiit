package slatekit.jobs

import slatekit.common.DateTime
import slatekit.common.ids.Identity
import slatekit.common.metrics.Counters
import slatekit.common.metrics.Lasts
import slatekit.results.Err
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class WorkerStats(val counters: Counters,
                  val lasts: Lasts<Task,WorkState, Err>) {



    val hasRun: Boolean get() = this.totalRuns.get() > 0

    val totalRuns = AtomicLong(0L)

    val totalRunsPassed   = AtomicLong(0L)

    val totalRunsFailed = AtomicLong(0L)

    val lastError = AtomicReference<Err>()

    val lastRunTime = AtomicReference<DateTime>()


    companion object {

        fun of(id: Identity):WorkerStats {
            return WorkerStats(Counters(id), Lasts(id))
        }
    }
}
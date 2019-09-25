package slatekit.jobs

import slatekit.common.DateTime
import slatekit.common.ids.Identity
import slatekit.common.metrics.Counters
import slatekit.common.metrics.Lasts
import slatekit.results.Err
import java.util.concurrent.atomic.AtomicReference

class WorkerStats(val counters: Counters,
                  val lasts: Lasts<Task,WorkState, Err>) {


    private val _runtime = AtomicReference<DateTime>()


    val hasRun: Boolean get() = this._runtime.get() != null

    val lastRunTime: DateTime? get() = this._runtime.get()


    companion object {

        fun of(id: Identity):WorkerStats {
            return WorkerStats(Counters(id), Lasts(id))
        }
    }
}
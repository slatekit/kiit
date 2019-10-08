package test.jobs

import slatekit.common.DateTime
import slatekit.jobs.Scheduler

class SampleScheduler : Scheduler{
    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        op()
    }

}
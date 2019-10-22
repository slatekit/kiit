package slatekit.jobs.support

import slatekit.common.DateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

interface Scheduler {

    suspend fun schedule(time:DateTime, op: suspend () -> Unit)
}



class DefaultScheduler(val scheduler:ScheduledExecutorService = scheduler(2)) : Scheduler {

    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        slatekit.common.TODO.IMPLEMENT("jobs", "scheduling")
    }

    companion object {

        fun scheduler(poolSize:Int):ScheduledExecutorService {
            return Executors.newScheduledThreadPool(poolSize)
        }
    }
}
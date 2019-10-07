package slatekit.jobs

import slatekit.common.DateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

interface Scheduler {

    suspend fun schedule(time:DateTime, op: suspend () -> Unit)
}



class DefaultScheduler(val scheduler:ScheduledExecutorService = scheduler(2)) : Scheduler {

    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
    }

    companion object {

        fun scheduler(poolSize:Int):ScheduledExecutorService {
            return Executors.newScheduledThreadPool(poolSize)
        }
    }
}
package slatekit.actors.pause

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class Scheduler(val scope: CoroutineScope,
                val scheduler: ScheduledExecutorService = scheduler(2)) {

    suspend fun schedule(seconds:Long, op: suspend () -> Unit) {
        scheduler.schedule({ runBlocking { op() } }, seconds, TimeUnit.SECONDS)
    }

    companion object {
        fun scheduler(poolSize: Int): ScheduledExecutorService {
            return Executors.newScheduledThreadPool(poolSize)
        }
    }
}

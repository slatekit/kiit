package kiit.jobs.support

import kotlinx.coroutines.runBlocking
import org.threeten.bp.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import slatekit.common.DateTime
import slatekit.common.ext.atUtc
import java.util.concurrent.TimeUnit

interface Scheduler {

    suspend fun schedule(time: DateTime, op: suspend () -> Unit)
}

class DefaultScheduler(val scheduler: ScheduledExecutorService = scheduler(2)) : Scheduler {

    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        val curr = DateTime.now().atUtc()
        val dest = time.atUtc()
        val diff = Duration.between(curr, dest)
        val secs = diff.seconds
        scheduler.schedule({
            runBlocking {
                op()
            }
        }, secs, TimeUnit.SECONDS)
    }

    companion object {

        fun scheduler(poolSize: Int): ScheduledExecutorService {
            return Executors.newScheduledThreadPool(poolSize)
        }
    }
}

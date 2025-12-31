package kiit.tasks

import kiit.common.DateTime
import kiit.common.ext.durationFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Used to schedule an action to execute
 */
interface Scheduler {
    suspend fun schedule(name:String, curr:DateTime, next:DateTime, op: suspend () -> Unit)
}

class DelayScheduler(val scope: CoroutineScope) : Scheduler {

    override suspend fun schedule(name:String, curr:DateTime, next:DateTime, op: suspend () -> Unit) {
        val diff = curr.durationFrom(next)
        val millis = diff.toMillis()
        delay(millis)
        scope.launch {
            op()
        }
    }
}
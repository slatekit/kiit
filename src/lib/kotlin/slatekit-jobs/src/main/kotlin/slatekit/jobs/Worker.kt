package slatekit.jobs

import java.util.concurrent.atomic.AtomicReference
import slatekit.common.Identity
import slatekit.common.Status


/**
 * Base class for Workers
 */
open class Worker<T>(
    override val id: Identity,
    override val stats: Recorder = Recorder.of(id),
    val operation: (suspend (Task) -> WorkResult)? = null
) : Workable<T> {

    protected val _status = AtomicReference<Pair<Status, String>>(Pair(Status.InActive, Status.InActive.name))

    /**
     * Transition current status to the one supplied
     */
    override suspend fun move(state: Status, note:String?) {
        move(state, note, true)
    }

    /**
     * Transition current status to the one supplied
     */
    suspend fun move(state: Status, note:String?, sendNotification:Boolean) {
        _status.set(Pair(state, note ?: state.name))
        if(sendNotification){
            notify(state.name, null)
        }
    }

    override fun status(): Status = _status.get().first

    override fun note():String = _status.get().second

    /**
     * Performs the work
     * @param task: The task to perform.
     * NOTE: If this worker manages it's own work load/queue/source, then this task is
     * provided by the work() method and assigned Task.owned
     */
    override suspend fun work(task: Task): WorkResult {
        return when (operation) {
            null -> WorkResult(WorkState.Done)
            else -> operation.invoke(task)
        }
    }
}

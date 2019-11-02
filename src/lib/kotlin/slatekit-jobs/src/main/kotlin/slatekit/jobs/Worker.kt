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

    private val _status = AtomicReference<Status>(Status.InActive)

    /**
     * Transition current status to the one supplied
     */
    override suspend fun move(state: Status) {
        _status.set(state)
        notify(state.name, null)
    }

    override fun status(): Status = _status.get()

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

package slatekit.core.scheduler

import slatekit.common.toId
import slatekit.core.scheduler.core.ErrorMode
import slatekit.core.scheduler.core.RunMode
import slatekit.common.Status
import slatekit.common.StatusSupport
import java.util.concurrent.atomic.AtomicReference

/**
 * Creates a Runnable task with all the specifications
 */
data class Task(val name:String, val runMode: RunMode, val errorMode: ErrorMode, val delay:Long, val call:() -> Unit) : StatusSupport {

    /**
     * Name converted to an id without spaces/lowercased
     * This is used for logging and as a metric id
     */
    val id = name.toId()


    /**
     * Current status of the task ( see status )
     */
    private val _status = AtomicReference<Status>(Status.InActive)


    /**
     * Gets the current status of the task
     */
    override fun status(): Status = _status.get()


    /**
     * Move the status to the one supplied.
     */
    override fun moveToState(state: Status): Status {
        _status.set(state)
        return _status.get()
    }
}
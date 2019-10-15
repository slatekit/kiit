package slatekit.functions.scheduler

import slatekit.common.toId
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
    private val status = AtomicReference<Status>(Status.InActive)


    /**
     * Gets the current status of the task
     */
    override fun status(): Status = status.get()


    /**
     * Move the status to the one supplied.
     */
    override fun transition(state: Status): Status {
        status.set(state)
        return status.get()
    }
}
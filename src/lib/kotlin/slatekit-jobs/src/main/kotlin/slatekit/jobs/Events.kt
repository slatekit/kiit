package slatekit.jobs

import slatekit.common.Identity
import slatekit.common.Status


sealed class Event {
    abstract val status: Status

    /**
     * @param id: Identity of the job
     * @param status: Status of the job
     * @param queue: Optional name of associated queue
     */
    data class JobEvent(val id: Identity, override val status: Status, val queue:String?) : Event()

    /**
     * @param id: Identity of the worker
     * @param status: Status of the worker
     * @param info: Info/diagnostics provided by the worker
     */
    data class WorkerEvent(val id: Identity, override val status: Status, val info:List<Pair<String, String>>) : Event ()
}


/**
 * Simple storage of subscriptions/observers interested in listening to state changes on jobs/workers
 */
class Events<T : Event> {
    private val changedSubscribers = mutableListOf<suspend (T) -> Unit>()
    private val statusSubscribers = mutableMapOf<String, MutableList<suspend (T) -> Unit>>()

    /**
     * Subscribe to any events
     */
    fun subscribe(op: suspend (T) -> Unit) {
        changedSubscribers.add(op)
    }

    /**
     * Subscribe to any events that match the given status
     */
    fun subscribe(status: Status, op: suspend (T) -> Unit) {
        val subs = if (statusSubscribers.containsKey(status.name)) {
            statusSubscribers[status.name] ?: mutableListOf()
        } else {
            val items = mutableListOf<suspend (T) -> Unit>()
            statusSubscribers[status.name] = items
            items
        }
        subs.add(op)
    }

    /**
     * Notify subscribers of the event
     */
    suspend fun notify(item: T) {
        notify(item, item.status)
    }

    /**
     * Notifies subscribers of the event that are listening for the specified status.
     */
    suspend fun notify(item: T, status: Status) {
        changedSubscribers.forEach { it.invoke(item) }
        if (statusSubscribers.containsKey(status.name)) {
            val subs = statusSubscribers[status.name]
            subs?.forEach { it.invoke(item) }
        }
    }
}

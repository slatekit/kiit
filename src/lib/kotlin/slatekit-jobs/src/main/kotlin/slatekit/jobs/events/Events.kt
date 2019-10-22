package slatekit.jobs.events

import slatekit.common.Status
import slatekit.functions.policy.Policy
import slatekit.jobs.Job
import slatekit.jobs.WorkRequest
import slatekit.jobs.WorkState
import slatekit.jobs.Worker


interface Events<T> {

    /**
     * Subscribes clients to a change of @see[slatekit.common.Status] on any item in this component
     * @param op:Operation to call when status is changed
     */
    suspend fun subscribe(op:suspend (T) -> Unit )


    /**
     * Subscribes clients to a change to the specific status @see[slatekit.common.Status]
     * @param op:Operation to call when status is changed
     */
    suspend fun subscribe(status: Status, op:suspend (T) -> Unit )
}



abstract class SubscribedEvents<T> : Events<T> {
    private val _changedSubscribers = mutableListOf<suspend (T) -> Unit>()
    private val _statusSubscribers = mutableMapOf<String, MutableList<suspend (T) -> Unit>>()


    override suspend fun subscribe(op: suspend (T) -> Unit) {
        _changedSubscribers.add(op)
    }


    override suspend fun subscribe(status: Status, op: suspend (T) -> Unit) {
        val subs = if(_statusSubscribers.containsKey(status.name)) {
            _statusSubscribers[status.name] ?: mutableListOf()
        } else {
            val items = mutableListOf<suspend (T) -> Unit>()
            _statusSubscribers[status.name] = items
            items
        }
        subs.add(op)
    }

    abstract suspend fun notify(item:T)


    suspend fun notify(item:T, status: Status) {
        _changedSubscribers.forEach { it.invoke(item) }
        if(_statusSubscribers.containsKey(status.name)) {
            val subs = _statusSubscribers[status.name]
            subs?.forEach { it.invoke(item) }
        }
    }
}
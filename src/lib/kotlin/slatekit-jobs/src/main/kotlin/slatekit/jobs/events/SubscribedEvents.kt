package slatekit.jobs.events

import slatekit.common.Status

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
            mutableListOf()
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
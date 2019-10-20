package slatekit.jobs

import slatekit.common.Status


interface Events<T> {

    /**
     * Subscribes clients to a change of @see[slatekit.common.Status] on any item in this component
     * @param op:Operation to call when status is changed
     */
    suspend fun onChange(op:suspend (T) -> Unit )


    /**
     * Subscribes clients to a change to the specific status @see[slatekit.common.Status]
     * @param op:Operation to call when status is changed
     */
    suspend fun onStatus(status: Status, op:suspend (T) -> Unit )
}



abstract class SubscribedEvents<T> : Events<T>{
    protected var _changedSubscribers = mutableListOf<suspend (T) -> Unit>()
    protected var _statusSubscribers = mutableMapOf<String, MutableList<suspend (T) -> Unit>>()


    override suspend fun onChange(op: suspend (T) -> Unit) {
        _changedSubscribers.add(op)
    }


    override suspend fun onStatus(status: Status, op: suspend (T) -> Unit) {
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



class JobEvents : SubscribedEvents<Job>(){

    override suspend fun notify(item:Job) {
        notify(item, item.status())
    }
}



class WorkerEvents(val workers: Workers) : SubscribedEvents<Worker<*>>(){

    override suspend fun notify(item:Worker<*>) {
        notify(item, item.status())
    }
}

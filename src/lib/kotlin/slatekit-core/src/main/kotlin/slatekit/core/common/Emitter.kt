package slatekit.core.slatekit.core.common

/**
 * Javascript like Event emitter
 * https://www.tutorialspoint.com/nodejs/nodejs_event_emitter.htm
 */
class Emitter<T> {
    /**
     * Gets all listeners in this emitter.
     * e.g.
     * 1. "job_1" : Listener1
     * 2. "job_1" : Listener2
     * 3. "job_2" : Listener3
     */
    data class Listener<T>(val name: String, val count: Int?, val isMin: Boolean, val call: suspend (T) -> Unit)

    private val _listeners = mutableMapOf<String, MutableList<Listener<T>>>()

    val listeners: List<Listener<T>>
        get() {
            return _listeners.values.flatten()
        }

    suspend fun emit(name:String, args:T) {
        process(name, create = false) { all ->
            all.forEach {
                it.call.invoke(args)
            }
        }
    }

    fun on(name: String, listener: suspend (T) -> Unit) {
        on(name, null, false, listener)
    }

    fun one(name: String, listener: suspend (T) -> Unit) {
        on(name, 1, false, listener)
    }

    fun min(name: String, count: Int, listener: suspend (T) -> Unit) {
        on(name, count, true, listener)
    }

    fun max(name: String, count: Int, listener: suspend (T) -> Unit) {
        on(name, count, false, listener)
    }

    fun on(name: String, count: Int?, isMin: Boolean, listener: suspend (T) -> Unit) {
        update(name, create = true) { all ->
            val newEntry = Listener<T>(name, count, isMin, listener)
            all.add(newEntry)
        }
    }

    fun remove(name: String, listener: suspend (T) -> Unit) {
        update(name, create = false) { all ->
            val ndx = all.indexOfFirst { it.call == listener }
            if (ndx >= 0) {
                all.removeAt(ndx)
            }
        }
    }

    fun removeAll(name: String) {
        _listeners.remove(name)
    }

    fun removeAll() {
        _listeners.clear()
    }

    private fun update(name:String, create:Boolean, op:(MutableList<Listener<T>>) -> Unit) {
        val listeners = filter(name, create)
        listeners?.let { op(it) }
    }

    private suspend fun process(name:String, create:Boolean, op:suspend (MutableList<Listener<T>>) -> Unit) {
        val listeners = filter(name, create)
        listeners?.let { op(it) }
    }

    private fun filter(name:String, create:Boolean) : MutableList<Listener<T>>? {
        val existing = _listeners[name] as MutableList<Listener<T>>?
        return when {
            existing != null -> existing
            !create -> existing
            else -> {
                val all = mutableListOf<Listener<T>>()
                _listeners[name] = all
                all
            }
        }
    }
}
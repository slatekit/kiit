package kiit.utils.events

import java.util.concurrent.atomic.AtomicLong

/**
 * Javascript like Event emitter
 * https://www.tutorialspoint.com/nodejs/nodejs_event_emitter.htm
 */
class Emitter<T> {

    private val _listeners = mutableMapOf<String, MutableList<Listener<T>>>()

    val listeners: List<Listener<T>>
        get() {
            return _listeners.values.flatten()
        }

    suspend fun emit(args: T) {
        emit(ALL, args)
    }

    suspend fun emit(name: String, args: T) {
        process(name, create = false) { all ->
            val removals = mutableListOf<Int>()
            all.forEachIndexed { ndx, listener ->
                when (listener.limit) {
                    null -> listener.call.invoke(args)
                    else -> {
                        val count = listener.count()
                        if (count >= listener.limit) {
                            removals.add(ndx)
                        } else {
                            listener.inc()
                            listener.call.invoke(args)
                        }
                    }
                }
            }
            // Remove items that passed their limit
            removals.sortDescending()
            removals.forEach { all.removeAt(it) }
        }
    }

    fun on(listener: suspend (T) -> Unit) {
        on(ALL, null, listener)
    }

    fun on(name: String, listener: suspend (T) -> Unit) {
        on(name, null, listener)
    }

    fun one(name: String, listener: suspend (T) -> Unit) {
        on(name, 1, listener)
    }

    fun max(name: String, count: Int, listener: suspend (T) -> Unit) {
        on(name, count, listener)
    }

    fun on(name: String, count: Int?, listener: suspend (T) -> Unit) {
        update(name, create = true) { all ->
            val newEntry = Listener<T>(name, count, listener)
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

    private fun update(name: String, create: Boolean, op: (MutableList<Listener<T>>) -> Unit) {
        val listeners = filter(name, create)
        listeners?.let { op(it) }
    }

    private suspend fun process(name: String, create: Boolean, op: suspend (MutableList<Listener<T>>) -> Unit) {
        val listeners = filter(name, create)
        listeners?.let { op(it) }
    }

    private fun filter(name: String, create: Boolean): MutableList<Listener<T>>? {
        val existing = _listeners[name]
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

    /**
     * Gets all listeners in this emitter.
     * e.g.
     * 1. "job_1" : Listener1
     * 2. "job_1" : Listener2
     * 3. "job_2" : Listener3
     */
    data class Listener<T>(val name: String, val limit: Int?, val call: suspend (T) -> Unit) {
        private val counter = AtomicLong(0L)

        fun hasLimit(): Boolean = limit != null
        fun inc() = counter.incrementAndGet()
        fun count(): Long = counter.get()
    }

    companion object {
        const val ALL = "*"
    }
}

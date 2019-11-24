package slatekit.tracking

import java.util.concurrent.atomic.AtomicReference

class Tracked<T> {
    private val stamped = AtomicReference<Updates<T>>(Updates<T>())


    fun get(): Updates<T> {
        return stamped.get()
    }


    fun set(newValue: T?) {
        val curr = get()
        stamped.set(curr.update(newValue))
    }
}

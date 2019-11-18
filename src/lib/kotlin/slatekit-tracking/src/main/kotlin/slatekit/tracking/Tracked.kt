package slatekit.tracking

import slatekit.common.DateTime
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class Tracked<T> {
    private val stamped = AtomicReference<Triple<DateTime, Long, T?>>(null)
    private val value = AtomicLong(0L)


    fun get(): Triple<DateTime, Long, T?> {
        return stamped.get()
    }


    fun set(newValue: T?) {
        val curr = value.incrementAndGet()
        stamped.set(Triple(DateTime.now(), curr, newValue))
    }
}

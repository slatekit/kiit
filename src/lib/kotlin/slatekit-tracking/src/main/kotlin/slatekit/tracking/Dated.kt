package slatekit.tracking

import slatekit.common.DateTime
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class Dated : Count {
    private val stamped = AtomicReference<Pair<DateTime, Long>>(null)
    private val value = AtomicLong(0L)

    override fun inc(): Long {
        val curr = value.incrementAndGet()
        stamped.set(Pair(DateTime.now(), curr))
        return curr
    }


    override fun dec(): Long {
        val curr = value.decrementAndGet()
        stamped.set(Pair(DateTime.now(), curr))
        return curr
    }


    override fun get(): Long {
        return stamped.get().second
    }


    override fun set(newValue: Long) {
        value.set(newValue)
        stamped.set(Pair(DateTime.now(), newValue))
    }
}



package slatekit.core.gate

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


open class Tracker {
    val batch = AtomicInteger(0)
    val count = AtomicInteger(0)
    val total = AtomicLong(0L)


    fun inc(): Int {
        total.incrementAndGet()
        return count.incrementAndGet()
    }


    /**
     * Moves the next batch, reset the existing counter to 0
     */
    open fun next() {
        batch.incrementAndGet()
        count.set(0)

        // Neede ? Sanity check
        val t = total.get()
        if(t > Long.MAX_VALUE - 100000) {
            total.set(0L)
        }
    }


    open fun reset() {
        batch.set(0)
        count.set(0)
        total.set(0L)
    }
}


class ErrorTracker : Tracker() {
    val err = AtomicReference<Exception>()


    fun inc(ex: Exception) {
        super.inc()
        err.set(ex)
    }


    override fun reset() {
        super.reset()
        err.set(null)
    }
}

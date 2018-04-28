package slatekit.core.gate

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

open class Limiter {
    val count = AtomicLong(0L)


    fun inc(): Long = count.incrementAndGet()
    fun dec(): Long = count.decrementAndGet()
    fun get(): Long = count.get()

    open fun reset() = count.set(0L)
}


class ErrorLimiter : Limiter() {
    val err = AtomicReference<Exception>()


    fun inc(ex: Exception) = {
        super.inc()
        err.set(ex)
    }


    override fun reset() {
        super.reset()
        err.set(null)
    }
}


class VolumeLimiter : Limiter() {
    val main = AtomicInteger(0)


    override fun reset() {
        super.reset()
        main.incrementAndGet()
    }
}
package slatekit.tracking

import slatekit.common.DateTime
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class Tracker<T>(value:T? = null, private val timeStampUpdateCount:Int = 0){
    private val current = AtomicReference<T?>(value)
    private val created = AtomicReference<DateTime?>(null)
    private val accessed = AtomicReference<DateTime?>(null)
    private val counts = AtomicLong(0)
    private val interval = AtomicInteger(0)

    fun value():T? = current.get()


    fun get(): Tracked<T> {
        return Tracked(current.get(), counts.get(), created.get(), accessed.get())
    }


    fun set(value:T?) {
        this.current.set(value)
        this.inc()
    }


    fun inc() {
        val count = counts.incrementAndGet()
        if (count > Long.MAX_VALUE - 10000) {
            counts.set(0L)
        }
        val acc = interval.incrementAndGet()
        if(acc >= timeStampUpdateCount || timeStampUpdateCount == 0) {
            accessed.set(DateTime.now())
            interval.set(0)
        } else {
            val currTs = accessed.get()
            if(currTs == null) {
                accessed.set(DateTime.now())
            }
        }
    }


    fun map(op:(T) -> T): Tracker<T> {
        return when(val value = current.get()) {
            null -> this
            else -> Tracker(op(value))
        }
    }
}

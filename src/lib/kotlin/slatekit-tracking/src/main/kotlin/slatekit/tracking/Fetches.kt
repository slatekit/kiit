package slatekit.tracking

import slatekit.common.DateTime
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


data class Fetched(@JvmField val count:Long,
                   @JvmField val timestamp:DateTime?)

/**
 * This track the number of times something was read/accessed
 * while also tracking the last timestamp of the read.
 *
 * For heavy reads, this provides a way to increment the counts
 * while changing the timestamp at periodic intervals to avoid
 * constant new DateTime allocations as an optimization.
 */
class Fetches(private val timeStampUpdateCount:Int) {
    private val stamped = AtomicReference<DateTime?>(null)
    private val counts = AtomicLong(0)
    private val interval = AtomicInteger(0)


    fun get(): Fetched {
        return Fetched(counts.get(), stamped.get())
    }

    fun inc() {
        val count = counts.incrementAndGet()
        if (count > Long.MAX_VALUE - 10000) {
            counts.set(0L)
        }
        val curr = interval.incrementAndGet()
        if(curr >= timeStampUpdateCount || timeStampUpdateCount == 0) {
            stamped.set(DateTime.now())
            interval.set(0)
        } else {
            val currTs = stamped.get()
            if(currTs == null) {
                stamped.set(DateTime.now())
            }
        }
    }
}

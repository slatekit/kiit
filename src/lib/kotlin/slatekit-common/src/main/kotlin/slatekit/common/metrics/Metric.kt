package slatekit.common.metrics

import slatekit.common.DateTime
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

interface Metric {
    val tags: List<Tag>
}


/**
 * Simple counter for a value
 */
data class Counter(override val tags: List<Tag>, val customTags:List<String>? = null) : Metric {

    val value = AtomicLong(0L)
    fun inc(): Long = value.incrementAndGet()
    fun dec(): Long = value.decrementAndGet()
    fun get(): Long = value.get()
}


/**
 * Simple gauge to check a value
 */
data class Gauge<T>(override val tags: List<Tag>,
                    val fetcher: () -> T,
                    val customTags:List<Tag>? = null,
                    val reloadSeconds: Long = 10) : Metric {

    private var value: T? = null
    private var lastTimeStamp = DateTime.now()

    fun get(): T? {
        if(isOld()) {
            refresh()
        }
        return value
    }


    fun refresh(){
        set(fetcher())
    }


    fun set(newValue:T){
        value = newValue
        lastTimeStamp = DateTime.now()
    }


    fun isOld():Boolean {
        val now = DateTime.now()
        val expires = lastTimeStamp.plusSeconds(reloadSeconds)
        return now > expires
    }
}


/**
 * Simple timer
 */
data class Timer(override val tags: List<Tag>, val customTags:List<String>? = null) : Metric {

    private val last = AtomicReference<Record>()
    private val value = AtomicLong(0L)

    fun record(call:() -> Unit ) {
        val start = DateTime.now()
        call()
        val end = DateTime.now()
        val diffMs = end.durationFrom(start).toMillis()
        last.set(Record(start.raw.toInstant(), end.raw.toInstant(), diffMs))
        value.incrementAndGet()
    }
}


data class Record(val start:Instant, val end: Instant, val diffMs:Long)
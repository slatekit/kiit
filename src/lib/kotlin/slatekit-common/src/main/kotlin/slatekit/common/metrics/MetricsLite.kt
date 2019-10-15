package slatekit.common.metrics

import org.threeten.bp.Instant
import org.threeten.bp.ZonedDateTime
import slatekit.common.ext.durationFrom
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


class MetricsLite(
        override val settings: MetricsSettings = MetricsSettings(true,true, Tags(listOf())),
        override val source: String = "slatekit-internal"
) : Metrics {
    private val counters = mutableMapOf<String, Counter>()
    private val gauges = mutableMapOf<String, Gauge<*>>()
    private val timers = mutableMapOf<String, Timer>()


    /**
     * The provider of the metrics ( Micrometer for now )
     */
    override fun provider(): Any = this


    override fun total(name: String): Double {
        return getOrCreate(name, counters, { Counter(globals(), null) } ).get().toDouble()
    }


    /**
     * Increment a counter
     */
    override fun count(name: String, tags: List<String>?) {
        getOrCreate(name, counters, { Counter(globals(), tags) } ).inc()
    }


    /**
     * Set value on a gauge
     */
    override fun <T> gauge(name: String, call: () -> T, tags: List<Tag>?) where T: kotlin.Number {
        getOrCreate(name, gauges, { Gauge(globals(), call, tags, 10) })
    }


    /**
     * Set value on a gauge
     */
    override fun <T> gauge(name: String, value:T) where T: kotlin.Number {
        gauges[name]?.let{ (it as Gauge<T>).set(value) }
    }


    /**
     * Times an event
     */
    override fun time(name: String, tags: List<String>?, call:() -> Unit ) {
        getOrCreate(name, timers, { Timer( globals(), tags) }).record(call)
    }


    val emptyGlobals = listOf<Tag>()
    private fun globals(): List<Tag> {
        return if (settings.standardize) settings.tags.global else emptyGlobals
    }


    private fun <T> getOrCreate(name:String, map:MutableMap<String, T>, creator:() -> T): T {
        return if(map.containsKey(name)){
            map[name]!!
        } else {
            val item = creator()
            map[name] = item
            item
        }
    }



    interface Metric {
        val tags: List<Tag>
    }



    /**
     * Simple counter for a value
     */
    data class Counter(override val tags: List<Tag>, val customTags:List<String>? = null) : Metric {

        private val value = AtomicLong(0L)

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
        private var lastTimeStamp = ZonedDateTime.now()

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
            lastTimeStamp = ZonedDateTime.now()
        }


        fun isOld():Boolean {
            val now = ZonedDateTime.now()
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
            val start = ZonedDateTime.now()
            call()
            val end = ZonedDateTime.now()
            val diffMs = end.durationFrom(start).toMillis()
            last.set(Record(start.toInstant(), end.toInstant(), diffMs))
            value.incrementAndGet()
        }



        data class Record(val start: Instant, val end: Instant, val diffMs:Long)
    }


    companion object {
        fun build(): MetricsLite {
            return MetricsLite(MetricsSettings(true,true, Tags(listOf())))
        }
    }
}
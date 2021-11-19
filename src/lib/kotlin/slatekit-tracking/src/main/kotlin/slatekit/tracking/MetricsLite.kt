package slatekit.tracking

import org.threeten.bp.Instant
import org.threeten.bp.ZonedDateTime
import slatekit.common.ext.durationFrom
import slatekit.common.Identity
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


/**
 * Simple / Light-Weight implementation of the Metric interface for quick/in-memory purposes.
 *
 * NOTES:
 * 1. There is a provider/wrapper for https://micrometer.io/ available in the slatekit.providers
 * 2. If you are already using micro-meter, then use the wrapper above.
 * 3. SlateKit custom diagnostics components ( Calls, Counters, Events, Lasts ) are orthogonal to metrics
 * 4. SlateKit has its own ( just a few ) diagnostic level components like Calls/Counters/Events/Lasts
 *    that are not available in other metrics libraries. These are specifically designed to work with
 *    the Result<T, E> component in @see[slatekit.results.Result] for counting/tracking successes/failures
 *
 */
class MetricsLite(
        override val id: Identity,
        val tags:List<Tag> = listOf(),
        override val source: String = "slatekit",
        override val settings: MetricsSettings = MetricsSettings(true, true, Tags(tags))
) : Metrics {

    private val counters = mutableMapOf<String, Counter>()
    private val gauges = mutableMapOf<String, Gauge<*>>()
    private val timers = mutableMapOf<String, Timer>()


    /**
     * The provider of the metrics ( Micrometer for now )
     */
    override val provider: Any = this


    override fun total(name: String): Double {
        return getOrCreate(name, counters) { Counter(globals(), null) }.get().toDouble()
    }


    /**
     * Increment a counter
     */
    override fun count(name: String, tags: List<String>?) {
        getOrCreate(name, counters) { Counter(globals(), tags) }.inc()
    }


    /**
     * Set value on a gauge
     */
    override fun <T> gauge(name: String, call: () -> T, tags: List<Tag>?) where T: kotlin.Number {
        getOrCreate(name, gauges) { Gauge(globals(), call, tags, 10) }
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
        getOrCreate(name, timers) { Timer(globals(), tags) }.record(call)
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


    /**
     * Simple timer
     */
    data class Timer(override val tags: List<Tag>, val customTags:List<String>? = null) : Tagged {

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
        fun build(id: Identity, tags:List<Tag> = listOf()): MetricsLite {
            return MetricsLite(id, tags, settings = MetricsSettings(true, true, Tags(listOf())))
        }
    }
}

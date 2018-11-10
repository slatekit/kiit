package slatekit.common.metrics

import java.lang.Number


class MetricsLite(
        override val settings: MetricsSettings = MetricsSettings(true, Tags(listOf())),
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


    companion object {
        fun build(): MetricsLite {
            return MetricsLite(MetricsSettings(true, Tags(listOf())))
        }
    }
}
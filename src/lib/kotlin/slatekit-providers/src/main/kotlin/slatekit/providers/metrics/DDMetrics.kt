package slatekit.providers.metrics

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.datadog.DatadogMeterRegistry
import slatekit.common.metrics.*


class DDMetrics(
        override val settings: MetricsSettings,
        override val source: String = "micrometer",
        val config:DDConfig
) : Metrics {

    val registry:MeterRegistry = DatadogMeterRegistry(config, Clock.SYSTEM)


    /**
     * Global / Common tags to supplied to all metrics
     */
    val globals:List<io.micrometer.core.instrument.Tag> = toTags(settings.tags.global)


    /**
     * The provider of the metrics ( Micrometer for now )
     */
    override fun provider(): Any = registry


    /**
     * Increment a counter
     */
    override fun count(name: String, tags: List<String>?) {
        registry.counter(name, *(tags?.toTypedArray() ?: arrayOf<String>()))
    }


    /**
     * Set value on a gauge
     */
    override fun <T> gauge(name: String, call: () -> T, tags: List<Tag>?) where T: kotlin.Number {
        registry.gauge(name, toTags(tags ?: listOf()), call(), { it -> it.toDouble() })
    }


    /**
     * Set value on a gauge
     */
    override fun <T> gauge(name: String, value:T) where T: kotlin.Number {
        registry.gauge(name, value)
    }


    /**
     * Times an event
     */
    override fun time(name: String, tags: List<String>?, call:() -> Unit ) {
        registry.timer(name, *(tags?.toTypedArray() ?: arrayOf<String>())).record(call)
    }


    val emptyGlobals = listOf<io.micrometer.core.instrument.Tag>()
    private fun globals(): List<io.micrometer.core.instrument.Tag>? {
        return if (settings.standardize) globals else emptyGlobals
    }


    companion object {
        fun toTags(tags: List<Tag>): List<io.micrometer.core.instrument.Tag> {
            return tags.map {
                io.micrometer.core.instrument.ImmutableTag(it.tagName, it.tagVal)
            }
        }
    }
}
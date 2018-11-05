package slatekit.providers.metrics

import io.micrometer.core.instrument.MeterRegistry


class MetricsLite(
        override val settings: MetricsSettings,
        override val source: String = "micrometer",
        val registry: MeterRegistry
) : Metrics {

    /**
     * Global / Common tags to supplied to all metrics
     */
    val globals:List<io.micrometer.core.instrument.Tag> = settings.tags.global.map {
        io.micrometer.core.instrument.ImmutableTag(it.name, it.value)
    }


    /**
     * The provider of the metrics ( Micrometer for now )
     */
    override fun provider(): Any = registry


    /**
     * Increment a counter
     */
    override fun count(name: String, tags: List<Tag>?) {
        registry.counter(name, globals())
    }


    /**
     * Set value on a gauge
     */
    override fun guage(name: String, tags: List<Tag>?) {
        registry.counter(name, globals())
    }


    /**
     * Track timer
     */
    override fun time(name: String, tags: List<Tag>?, call:() -> Unit ) {
        registry.timer(name, globals()).record( call )
    }


    val emptyGlobals = listOf<io.micrometer.core.instrument.Tag>()
    private fun globals(): List<io.micrometer.core.instrument.Tag>? {
        return if (settings.standardize) globals else emptyGlobals
    }
}
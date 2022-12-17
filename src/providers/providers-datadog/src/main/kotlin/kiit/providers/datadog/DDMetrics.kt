package kiit.providers.datadog

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.datadog.DatadogMeterRegistry
import slatekit.common.Identity
import slatekit.common.Provider
import slatekit.telemetry.*


class DDMetrics(val registry: MeterRegistry,
                override val id: Identity,
                override val settings: MetricsSettings) : Metrics {


    override val source: String = "micrometer"
    val emptyLocalTags = arrayOf<String>()


    override val provider: Any = registry


    override fun total(name: String): Double {
        return if(settings.enabled)
            registry.counter(name, listOf()).count()
        else
            0.0
    }

    /**
     * Increment a counter
     */
    override fun count(name: String, tags: List<String>?) {
        if(settings.enabled) {
            val counter = registry.counter(name, *(tags?.toTypedArray() ?: emptyLocalTags))
            counter.increment()
        }
    }


    /**
     * Set value on a gauge
     */
    override fun <T> gauge(name: String, call: () -> T, tags: List<Tag>?) where T: kotlin.Number {
        if(settings.enabled){
            registry.gauge(name, toTags(tags ?: listOf()), call(), { it -> it.toDouble() })
        }
    }


    /**
     * Set value on a gauge
     */
    override fun <T> gauge(name: String, value:T) where T: kotlin.Number {
        if(settings.enabled) {
            registry.gauge(name, value)
        }
    }


    /**
     * Times an event
     */
    override fun time(name: String, tags: List<String>?, call:() -> Unit ) {
        if(settings.enabled){
            registry.timer(name, *(tags?.toTypedArray() ?: emptyLocalTags)).record(call)
        }
    }


    companion object {
        fun toTags(tags: List<Tag>): List<io.micrometer.core.instrument.Tag> {
            return tags.map {
                io.micrometer.core.instrument.ImmutableTag(it.tagName, it.tagVal)
            }
        }

        fun build(settings:MetricsSettings, config:DDConfig, bindMetrics:Boolean):MeterRegistry {
            val registry = DatadogMeterRegistry(config, Clock.SYSTEM)
            if(settings.standardize) {
                val globalTags = toTags(settings.tags.global).toMutableList()
                registry.config().commonTags(globalTags)
            }
            if(settings.enabled && bindMetrics) {
                ClassLoaderMetrics().bindTo(registry)
                JvmMemoryMetrics().bindTo(registry)
                JvmGcMetrics().bindTo(registry)
                ProcessorMetrics().bindTo(registry)
                JvmThreadMetrics().bindTo(registry)
            }
            return registry
        }
    }
}
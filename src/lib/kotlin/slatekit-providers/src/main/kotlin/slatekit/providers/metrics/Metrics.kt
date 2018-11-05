package slatekit.providers.metrics

import kotlin.reflect.KClass


/**
 * Used for metrics internal to SlateKit
 * Simple interface to metrics with ( current ) dependency on Micrometer.
 *
 * NOTES:
 * 1. Standardization on the tags
 * 2. Not currently sure whether to go w/ Drop-Wizard or Micrometer.
 * 3. Basic counters, timer.record are enough for most INTERNAL Slate Kit metrics gathering
 */
interface Metrics {

    val settings:MetricsSettings

    val source:String

    fun provider():Any

    fun count(name: String, tags:List<Tag>?)

    fun guage(name: String, tags:List<Tag>?)

    fun time(name: String, tags: List<Tag>?, call:() -> Unit )
}


//
// import com.codahale.metrics.MetricRegistry
//interface Metrics {
//    val group: String?
//    val registry: MetricRegistry
//
//    fun meter(name: String) = registry.meter(fullName(name))
//    fun meter(cls: KClass<*>, name: String? = null) = registry.meter(fullName(cls, name))
//
//    fun counter(name: String) = registry.counter(fullName(name))
//    fun counter(cls: KClass<*>, name: String? = null) = registry.counter(fullName(cls, name))
//
//    fun guage(name: String) = registry.counter(fullName(name))
//    fun guage(cls: KClass<*>, name: String? = null) = registry.counter(fullName(cls, name))
//
//    fun timer(name: String) = registry.timer(fullName(name))
//    fun timer(cls: KClass<*>, name: String? = null) = registry.timer(fullName(cls, name))
//
//    fun histogram(name: String) = registry.histogram(fullName(name))
//    fun histogram(cls: KClass<*>, name: String? = null) = registry.histogram(fullName(cls, name))
//
//
//    fun fullName(name: String): String = MetricRegistry.name(group, name)
//    fun fullName(cls: KClass<*>, name: String?): String = MetricRegistry.name(cls.java, name)
//
//
//    fun summary(): List<Pair<String,Any>> {
//        val counters = registry.counters.keys.map { name -> Pair(name, registry.counter(name).count) }
//        val meters = registry.meters.keys.map { name -> Pair(name, registry.meter(name).count) } +
//                registry.meters.keys.map { name -> Pair(name, registry.meter(name).meanRate) } +
//                registry.meters.keys.map { name -> Pair(name, registry.meter(name).oneMinuteRate) } +
//                registry.meters.keys.map { name -> Pair(name, registry.meter(name).fiveMinuteRate) } +
//                registry.meters.keys.map { name -> Pair(name, registry.meter(name).fifteenMinuteRate) }
//
//        val timers = registry.timers.keys.map { name -> Pair(name, registry.timer(name).count) } +
//                registry.timers.keys.map { name -> Pair(name, registry.timer(name).meanRate) } +
//                registry.timers.keys.map { name -> Pair(name, registry.timer(name).oneMinuteRate) } +
//                registry.timers.keys.map { name -> Pair(name, registry.timer(name).fiveMinuteRate) } +
//                registry.timers.keys.map { name -> Pair(name, registry.timer(name).fifteenMinuteRate) }
//
//        val histograms = registry.timers.keys.map { name -> Pair(name, registry.timer(name).count) }
//        return counters + meters + timers + histograms
//    }
//}



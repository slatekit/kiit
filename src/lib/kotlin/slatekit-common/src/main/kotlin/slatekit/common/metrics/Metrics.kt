package slatekit.common.metrics

import java.lang.Number


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

    fun count(name: String, tags:List<String>?)

    fun time(name: String, tags: List<String>?, call:() -> Unit )

    fun <T> gauge(name: String, value:T) where T: kotlin.Number

    fun <T> gauge(name: String, call: () -> T, tags: List<Tag>?) where T: kotlin.Number
}


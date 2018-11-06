package slatekit.common.metrics

/**
 * Represents a single tag ( name/value pair ) to associate metrics with
 */
interface Tag {
    val tagName:String
    val tagVal:String
}


class MetricTag(override val tagName:String, override val tagVal:String):Tag
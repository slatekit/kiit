package slatekit.common.throttle


/**
 * Represents a range in time from start/finish
 * @param name  : Describe the span  e.g. "hours", "minutes", "seconds"
 * @param start : Start  value       e.g. 8  => 8 am
 * @param finish: Finish value       e.g. 14 => 2 pm
 */
data class Span(val name:String, val start:Int, val finish:Int) {

    /**
     * Whether the time supplied falls within this timespan.
     * @param value
     * @return
     */
    fun isWithin(value:Int):Boolean = value in start..finish
}

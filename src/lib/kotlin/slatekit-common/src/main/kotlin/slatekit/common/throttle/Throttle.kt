package slatekit.common.throttle


/**
 * Represents a throttle to control the speed/throughput of something
 * @param name     : Name for the throttle         e.g. "breakfast"
 * @param span     : Time span / range associated  e.g. "10:00" to "14:00" => 10am to 2pm
 * @param rate     : Rate description              e.g. Low
 * @param settings : Additional throttle settings  e.g. derived classes to implement
 */
data class Throttle(val name:String, val span: Span, val rate: Rate, val settings:ThrottleSettings)


interface ThrottleSettings


/**
 * Default for no additional throttle settings.
 * But an implementation could be e.g.
 * 1. the batch size of fetching records
 * 2. pausing time between iterations
 */
object NoSettings : ThrottleSettings


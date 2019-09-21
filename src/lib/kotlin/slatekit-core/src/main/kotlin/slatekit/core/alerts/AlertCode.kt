package slatekit.core.alerts

/**
 * Used for color coding/specifying intent
 */
interface AlertCode {
    val name :String
    val value :Int
    val color:String
}


/**
 * Default codes
 * NOTE: These are NOT setup as a sealed class, as they should be extendable/customizable.
 */
object AlertCodes {
    object Normal   : AlertCode { override val name = "normal"  ; override val value = 0; override val color = "#3498db"; }
    object Pending  : AlertCode { override val name = "pending" ; override val value = 0; override val color = "#F0E68C"; }
    object Success  : AlertCode { override val name = "success" ; override val value = 1; override val color = "#2ecc71"; }
    object Failure  : AlertCode { override val name = "failure" ; override val value = 2; override val color = "#e74c3c"; }
    object Stopped  : AlertCode { override val name = "stopped" ; override val value = 3; override val color = "#e67e22"; }
    object Complete : AlertCode { override val name = "complete"; override val value = 4; override val color = "#2c3e50"; }
}
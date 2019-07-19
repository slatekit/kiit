package slatekit.core.alerts

/**
 * Represents a field that is part of the alert.
 * @param name : Name of the field e.g. "env"
 * @param value: Value of the field e.g. "dev"'
 * @param tags : Optional tags associated w/ the data
 */
data class AlertField(val name:String,
                      val value:Any?,
                      val tags:List<String>? = null)
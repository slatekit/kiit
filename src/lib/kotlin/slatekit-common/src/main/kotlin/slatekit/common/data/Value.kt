package slatekit.common.data

/**
 * Used for encoding a field/property from a class into name/value pair
 */
data class Value(val name:String, val tpe:DataType, val value:Any?, val text:String? = null)


/**
 * Represents the values of a model as a list of key/value pairs
 */
typealias Values = List<Value>

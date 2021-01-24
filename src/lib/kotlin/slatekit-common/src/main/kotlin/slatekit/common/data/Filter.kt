package slatekit.common.data


/**
 * Used to build simple conditions like "category = 2" for filters
 * @param name : Name of the field e.g. "id"
 * @param op   : Operator such as ">, >=, <, <=, not"
 * @param value: Value to for the condition
 */
data class Filter(val name:String, val op:Op, val value:Any?)

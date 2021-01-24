package slatekit.data.syntax

import slatekit.common.data.Encoding
import slatekit.common.data.Op


class Filters(val ops:Ops = Ops(), val left:String = "[", val right:String = "]") {

    /**
     * Builds a condition expression such as "id > 2"
     * @param field: Name of the field e.g. "id"
     * @param op   : Operator such as ">, >=, <, <=, not"
     * @param value: Value to for the condition
     */
    fun build(field:String, op:Op, value:Any?, surround:Boolean, placehoder:Boolean):String {
        val fieldName = Encoding.ensureField(field)
        val col = if (surround) left + fieldName + right else fieldName
        val comp = ops.toOp(op)
        val result = if(placehoder) "?" else Encoding.convertVal(value)
        val sql = "$col $comp $result"
        return sql
    }


    /**
     * Builds a condition expression such as "id > 2"
     * @param field: Name of the field e.g. "id"
     * @param value: Value to for the condition
     */
    fun update(field:String, value:Any?, surround:Boolean, placehoder:Boolean):String {
        val fieldName = Encoding.ensureField(field)
        val col = if (surround) left + fieldName + right else fieldName
        val result = if(placehoder) "?" else Encoding.convertVal(value)
        val sql = "$col = $result"
        return sql
    }
}


package slatekit.data.statements

import slatekit.common.data.Encoding
import slatekit.common.data.Op


class Expr(val ops:Ops, val left:String = "[", val right:String = "]") {

    /**
     * Builds a condition expression such as "id > 2"
     * @param field: Name of the field e.g. "id"
     * @param op   : Operator such as ">, >=, <, <=, not"
     * @param value: Value to for the condition
     */
    fun build(field:String, op:Op, value:Any?, surround:Boolean):String {
        val fieldName = Encoding.ensureField(field)
        val col = if (surround) left + fieldName + right else fieldName
        val comp = ops.toOp(op)
        val result = Encoding.convertVal(value)
        val sql = "$col $comp $result"
        return sql
    }
}


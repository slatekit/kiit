package slatekit.data.syntax

import slatekit.common.data.Compare

open class Ops {
    open fun toOp(op: Compare):String {
        return when (op) {
            Compare.Gt -> ">"
            Compare.Gte -> ">="
            Compare.Lt -> "<"
            Compare.Lte -> "<="
            Compare.Eq -> "is"
            Compare.Neq -> "is not"
            Compare.In -> "in"
            else   -> "is"
        }
    }
}

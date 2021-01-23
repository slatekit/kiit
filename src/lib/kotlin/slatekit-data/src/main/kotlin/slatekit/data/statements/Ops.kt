package slatekit.data.statements

import slatekit.common.data.Op

open class Ops {
    open fun toOp(op: Op):String {
        return when (op) {
            Op.Gt -> ">"
            Op.Gte -> ">="
            Op.Lt -> "<"
            Op.Lte -> "<="
            Op.Eq -> "is"
            Op.Neq -> "is not"
            Op.In -> "in"
            else   -> "is"
        }
    }
}

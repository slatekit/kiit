package slatekit.data.syntax

import slatekit.query.Op

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

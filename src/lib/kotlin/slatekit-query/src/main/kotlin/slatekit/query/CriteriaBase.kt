package slatekit.query


@Suppress("UNCHECKED_CAST")
open class CriteriaBase<T>(
    protected val converter: ((String) -> String)? = null,
    protected val encoder:((String) -> String)? = null) : Criteria<T> {
    protected val conditions= mutableListOf<Expr>()

    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The field name
     * @param op: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun where(field: String, op: Op, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        val condition = buildCondition(field, op, finalValue)
        conditions.add(condition)
        return this as T
    }

    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The field name
     * @param op: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun and(field: String, op: Op, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        val cond = buildCondition(field, op, finalValue)
        group(Logic.And, cond)
        return this as T
    }

    /**
     * adds an or clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun or(field: String, op: Op, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        val cond = buildCondition(field, op, finalValue)
        group(Logic.Or, cond)
        return this as T
    }


    protected fun buildCondition(field: String, op: Op, fieldValue: Any): Condition {
        val col = QueryEncoder.ensureField(field)
        val comparison = if (fieldValue == Const.Null) {
            val comp = when (op) {
                Op.Eq  -> "is"
                Op.Neq -> "is not"
                Op.In  -> "in"
                else   -> "is"
            }
            Pair(comp, Const.Null)
        } else {
            val comp = QueryEncoder.ensureCompare(op.text)
            Pair(comp, fieldValue)
        }
        val con = Condition(col, op, comparison.second)
        return con
    }

    override fun group(op: Logic, condition: Condition) {
        // Pop the last one
        val last = conditions.size - 1
        val left = conditions[last]
        conditions.removeAt(last)

        // Build a binary condition from left and right
        val group = LogicalExpr(left, op, condition)

        // Push back on condition list
        conditions.add(group)
    }
}

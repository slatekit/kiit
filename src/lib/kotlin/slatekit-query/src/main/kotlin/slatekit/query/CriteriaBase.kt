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
     * @param op: The comparison operator ( =, >, >=, <, <=, !=, in )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun where(field: String, op: String, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        val condition = buildCondition(field, op, finalValue)
        conditions.add(condition)
        return this as T
    }

    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun where(field: String, compare: Op, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        return where(field, compare.text, finalValue)
    }

    /**
     * adds an and clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun and(field: String, compare: String, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        val cond = buildCondition(field, compare, finalValue)
        group("and", cond)
        return this as T
    }

    override fun and(field: String, compare: Op, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        return and(field, compare.text, finalValue)
    }

    /**
     * adds an or clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun or(field: String, compare: String, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        val cond = buildCondition(field, compare, finalValue)
        group("or", cond)
        return this as T
    }

    override fun or(field: String, compare: Op, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        return or(field, compare.text, finalValue)
    }



    protected fun buildCondition(field: String, op: String, fieldValue: Any): Condition {
        val col = QueryEncoder.ensureField(field)
        val comparison = if (fieldValue == Const.Null) {
            val comp = when (op) {
                "=" -> "is"
                "is" -> "is"
                "!=" -> "is not"
                "<>" -> "is not"
                "in" -> "in"
                else -> "is"
            }
            Pair(comp, "null")
        } else {
            val comp = QueryEncoder.ensureCompare(op)
            Pair(comp, fieldValue)
        }
        val con = Condition(col, comparison.first, comparison.second)
        return con
    }

    override fun group(op: String, condition: Condition) {
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


/**
 * Base interface to support "where" conditions for
 * 1. selects
 * 2. updates
 * 3. deletes
 */
interface Criteria2<T> {
    fun where(field: String, compare: String, fieldValue: Any?): T

    fun where(field: String, compare: Op, fieldValue: Any?): T

    fun and(field: String, compare: String, fieldValue: Any?): T

    fun and(field: String, compare: Op, fieldValue: Any?): T

    fun or(field: String, compare: String, fieldValue: Any?): T

    fun or(field: String, compare: Op, fieldValue: Any?): T
}


//interface Where : Criteria<Where> {
//
//}
//
//class WhereImpl : Where {
//    val conditions = mutableListOf<Triple<String, String, Any?>>()
//
//    override fun where(field: String, compare: String, fieldValue: Any?): Where {
//        conditions.add(Triple(field, compare, fieldValue))
//        return this
//    }
//
//    override fun where(field: String, compare: Op, fieldValue: Any?): Where {
//        conditions.add(Triple(field, compare.text, fieldValue))
//        return this
//    }
//
//    override fun and(field: String, compare: String, fieldValue: Any?): Where {
//        conditions.add(Triple(field, compare, fieldValue))
//        return this
//    }
//
//    override fun and(field: String, compare: Op, fieldValue: Any?): Where {
//        conditions.add(Triple(field, compare.text, fieldValue))
//        return this
//    }
//
//    override fun or(field: String, compare: String, fieldValue: Any?): Where {
//        conditions.add(Triple(field, compare, fieldValue))
//        return this
//    }
//
//    override fun or(field: String, compare: Op, fieldValue: Any?): Where {
//        conditions.add(Triple(field, compare.text, fieldValue))
//        return this
//    }
//}
//
//
//class Storage<T>(val sample:T) {
//    fun build(where:Where.() -> Unit): Where {
//        val w = WhereImpl()
//        where(w)
//        return w
//    }
//
//    fun one(where:Where.() -> Unit): T {
//        val w = WhereImpl()
//        where(w)
//        return sample
//    }
//}

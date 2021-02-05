package slatekit.query


@Suppress("UNCHECKED_CAST")
open class CriteriaBase<T>(
    protected val converter: ((String) -> String)? = null,
    protected val encoder:((String) -> String)? = null) : Criteria<T> {
    protected var where:Expr? = null
    protected var source:String? = null
    protected var limit: Int? = null
    protected val orders = mutableListOf<Pair<String, Order>>()


    /**
     * Source to apply the criteria ( e.g. table name )
     */
    override fun from(source:String):T  {
        this.source = source
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
    override fun where(field: String, op: Op, fieldValue: Any?): T {
        val finalValue = fieldValue ?: Const.Null
        val condition = condition(field, op, finalValue)
        when(where == null) {
            true -> where = condition
            false -> combine(Logic.And, condition)
        }
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
        val cond = condition(field, op, finalValue)
        combine(Logic.And, cond)
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
        val cond = condition(field, op, finalValue)
        combine(Logic.Or, cond)
        return this as T
    }


    /**
     * Limit x number of records
     */
    override fun limit(max: Int): T  {
        this.limit = max
        return this as T
    }


    /**
     * Order by field1 asc, field2 desc
     */
    override fun orderBy(field: String, order: Order): T {
        this.orders.add(Pair(field, order))
        return this as T
    }


    protected fun condition(field: String, op: Op, fieldValue: Any): Condition {
        return Condition(field, op, fieldValue)
    }

    protected fun combine(logic:Logic, condition:Condition) {
        where?.let { where = LogicalExpr(it, logic, condition) }
    }
}

package slatekit.query

interface Criteria<T> {
    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The field name
     * @param op: The comparison operator ( =, >, >=, <, <=, !=, in )
     * @param fieldValue: The field value
     * @return this instance
     */
    fun where(field: String, op: String, fieldValue: Any?): T

    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    fun where(field: String, compare: Op, fieldValue: Any?): T

    /**
     * adds an and clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    fun and(field: String, compare: String, fieldValue: Any?): T
    fun and(field: String, compare: Op, fieldValue: Any?): T

    /**
     * adds an or clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    fun or(field: String, compare: String, fieldValue: Any?): T
    fun or(field: String, compare: Op, fieldValue: Any?): T
    fun group(op: String, condition: Condition)
}

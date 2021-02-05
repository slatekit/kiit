package slatekit.query

interface Criteria<T> {
    /**
     * Source to apply the criteria ( e.g. table name )
     */
    fun from(source:String): T

    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    fun where(field: String, op: Op, fieldValue: Any?): T

    /**
     * adds an and clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    fun and(field: String, op: Op, fieldValue: Any?): T

    /**
     * adds an or clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    fun or(field: String, op: Op, fieldValue: Any?): T


    /**
     * Limit x number of records
     */
    fun limit(max: Int): T


    /**
     * Order by field1 asc, field2 desc
     */
    fun orderBy(field: String, order: Order): T
}

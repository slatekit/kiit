package slatekit.data.features

import slatekit.query.Op

/**
 * Supports deletion of records using conditions
 */
interface Deletable<TId, T> where TId : Comparable<TId> {
    /**
     * deletes items based on the field name and value
     * @param field: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(field: String, value: Any?): Int = deleteByField(field, Op.Eq, value)


    /**
     * deletes items based on the field name, operator and value
     * @param field: The property reference
     * @param op   : The operator to check
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(field: String, op: Op, value: Any?): Int = deleteByFields(listOf(Triple(field, op, value)))


    /**
     * Deletes items based on the conditions provided
     */
    fun deleteByFields(conditions: List<Triple<String, Op, Any?>>): Int
}

package slatekit.data.features

import slatekit.common.data.Compare
import slatekit.common.data.Filter
import slatekit.common.data.Logical

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
    fun deleteByField(field: String, value: Any?): Int = deleteByField(field, Compare.Eq, value)


    /**
     * deletes items based on the field name, operator and value
     * @param field: The property reference
     * @param op   : The operator to check
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(field: String, op: Compare, value: Any?): Int = deleteByFilters(listOf(Filter(field, op, value)), Logical.And)


    /**
     * Deletes items based on the filters and logical operator
     * @param filters: The list of filters "id = 2" e.g. listOf( Filter("id", Op.Eq, "2" )
     * @param logical: The logical operator to use  e.g. "And | Or"
     */
    fun deleteByFilters(filters: List<Filter>, logical:Logical): Int
}

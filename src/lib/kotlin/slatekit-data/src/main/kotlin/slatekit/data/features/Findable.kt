package slatekit.data.features

import slatekit.common.data.Filter
import slatekit.common.data.Logical

/**
 * Supports finding records by conditions
 */
interface Findable<TId, T> where TId : Comparable<TId> {
    /**
     * finds items based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    fun findByField(field: String, op: Op, value: Any): List<T> = findByFilters(listOf(Filter(field, op, value)), Logical.And)


    /**
     * finds items based on the field in the values provided
     * @param field: name of field
     * @param value: values of field to search against
     * @return
     */
    fun findIn(field: String, value: List<Any>): List<T> = findByFilters(listOf(Filter(field, Op.In, value)), Logical.And)


    /**
     * finds items based on the conditions
     * @param filters: The list of filters "id = 2" e.g. listOf( Filter("id", Op.Eq, "2" )
     * @param logical: The logical operator to use  e.g. "And | Or"
     */
    fun findByFilters(filters: List<Filter>, logical: Logical): List<T>


    /**
     * finds first item based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    fun findOneByField(field: String, value: Any): T? = findOneByField(field, Op.Eq, value)


    /**
     * finds first item based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    fun findOneByField(field: String, op: Op, value: Any): T? = findByFilters(listOf(Filter(field, op, value)), Logical.And).firstOrNull()
}

package slatekit.data.features

import slatekit.query.Op

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
    fun findByField(field: String, op: Op, value: Any): List<T> = findByFields(listOf(Triple(field, op, value)))


    /**
     * finds items based on the field in the values provided
     * @param field: name of field
     * @param value: values of field to search against
     * @return
     */
    fun findIn(field: String, value: List<Any>): List<T> = findByFields(listOf(Triple(field, Op.In, value)))


    /**
     * finds items based on the conditions
     */
    fun findByFields(conditions: List<Triple<String, Op, Any>>): List<T>


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
    fun findOneByField(field: String, op: Op, value: Any): T? = findByFields(listOf(Triple(field, op, value))).firstOrNull()
}

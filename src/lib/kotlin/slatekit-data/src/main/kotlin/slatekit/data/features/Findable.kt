package slatekit.data.features

import slatekit.query.Op

interface Findable<TId, T> where TId : Comparable<TId> {
    /**
     * finds items based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    fun findByField(field: String, op: Op, value: Any): List<T> = listOf()


    /**
     * finds items based on the conditions
     */
    fun findByFields(conditions: List<Pair<String, Any>>): List<T> = listOf()


    /**
     * finds items based on the field in the values provided
     * @param field: name of field
     * @param value: values of field to search against
     * @return
     */
    fun findIn(field: String, value: List<Any>): List<T> = listOf()


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
    fun findOneByField(field: String, op: Op, value: Any): T? = null
}

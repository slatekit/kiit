package slatekit.data.features

import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query

/**
 * Supports finding records by conditions
 */
interface Findable<TId, T> where TId : Comparable<TId> {
    /**
     * finds items based on the field
     * @param field: name of field
     * @param value: value of field to search against
     * @return
     */
    fun findByField(field: String, value: Any?): List<T> = findByField(field, Op.Eq, value)


    /**
     * finds items based on the field
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    fun findByField(field: String, op: Op, value: Any?): List<T> = findByQuery(Query().where(field, op, value))


    /**
     * finds items based on the field in the values provided
     * @param field: name of field
     * @param value: values of field to search against
     * @return
     */
    fun findIn(field: String, value: List<Any>): List<T> = findByQuery(Query().where(field, Op.In, value))


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
    fun findOneByField(field: String, op: Op, value: Any): T? = findByQuery(Query().where(field, op, value).limit(1)).firstOrNull()


    /**
     * finds items based on the conditions
     * @param query: The list of filters "id = 2" e.g. listOf( Filter("id", Op.Eq, "2" )
     */
    fun findByQuery(query: IQuery): List<T>
}

package slatekit.data.features

import slatekit.query.Op
import slatekit.query.IQuery
import slatekit.query.Query

/**
 * Supports deletion of records using conditions
 */
interface Deletable<TId, T> where TId : Comparable<TId> {

    /**
     * deletes all entities from the data store using the ids
     * @param ids
     * @return
     */
    fun deleteAll(): Long


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
    fun deleteByField(field: String, op: Op, value: Any?): Int = deleteByQuery(Query().where(field, op, value))


    /**
     * Deletes items based on the filters and logical operator
     * @param query: The query builder to build up dynamic queries
     */
    fun deleteByQuery(query: IQuery): Int
}

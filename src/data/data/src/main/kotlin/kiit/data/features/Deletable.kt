package kiit.data.features

import kiit.query.Op
import kiit.query.Delete
import kiit.query.Update

/**
 * Supports deletion of records using conditions
 */
interface Deletable<TId, T> : Inspectable<TId, T> where TId : Comparable<TId>, T: Any {

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
    fun deleteByField(field: String, op: Op, value: Any?): Int = deleteByQuery(delete().where(field, op, value))


    /**
     * Deletes using a query builder
     * delete { where("level", Op.Eq, 3).and("active", Op.Eq, true) }
     */
    fun delete(builder: Delete.() -> Unit): Int {
        val q = delete()
        builder(q)
        return deleteByQuery(q)
    }

    /**
     * Deletes items based on the filters and logical operator
     * @param builder: The query builder to build up dynamic queries
     */
    fun deleteByQuery(builder: Delete): Int
}

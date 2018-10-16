package slatekit.entities.services

import slatekit.common.query.IQuery
import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport
import kotlin.reflect.KProperty

interface EntityDeletes<T> : ServiceSupport<T> where T : Entity {

    /**
     * deletes the entity
     *
     * @param entity
     */
    fun delete(entity: T?): Boolean {
        return entityRepo().delete(entity)
    }


    /**
     * deletes the entity
     *
     * @param entity
     */
    fun delete(ids:List<Long>): Int {
        return entityRepo().delete(ids)
    }


    /**
     * deletes the entity by its id
     * @param id
     * @return
     */
    fun deleteById(id: Long): Boolean {
        return entityRepo().delete(id)
    }


    /**
     * deletes items based on the field value
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun deleteByField(prop: KProperty<*>, value: Any): Int {
        return entityRepo().deleteByField(prop.name, value)
    }


    /**
     * updates items using the query
     */
    fun deleteByQuery(query: IQuery): Int {
        return entityRepo().deleteByQuery(query)
    }
}
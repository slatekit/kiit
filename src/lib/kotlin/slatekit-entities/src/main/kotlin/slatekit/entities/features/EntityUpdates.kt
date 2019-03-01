package slatekit.entities.features

import slatekit.common.DateTime
import slatekit.query.IQuery
import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.meta.Reflector
import slatekit.meta.kClass
import slatekit.entities.core.EntityEvent
import kotlin.reflect.KProperty

interface EntityUpdates<TId, T> : ServiceSupport<TId, T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    /**
     * updates the entity in the datastore
     * @param entity
     * @return
     */
    fun update(entity: T): Boolean {
        val original:T? = if (this is EntityHooks ) repoT().get(entity.identity()) else null
        val finalEntity = applyFieldData(2, entity)
        val success = repoT().update(finalEntity)

        // Event out
        if ( this is EntityHooks) {
            when(success) {
                true -> this.onEntityEvent(EntityEvent.EntityUpdated(original ?: entity, entity, DateTime.now()))
                else -> this.onEntityEvent(EntityEvent.EntityErrored(entity,
                        Exception("unable to update: " + entity.toString()), DateTime.now()))
            }
        }
        return success
    }

    /**
     * updates the entity field in the datastore
     * @param id: id of the entity
     * @param field: the name of the field
     * @param value: the value to set on the field
     * @return
     */
    fun update(id: TId, field: String, value: String) {
        val item = repoT().get(id)
        item?.let { entity ->
            Reflector.setFieldValue(entity.kClass, entity, field, value)
            update(entity)
        }
    }

    /**
     * updates items based on the field name
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun updateByField(prop: KProperty<*>, value: Any): Int {
        return repoT().updateByField(prop.name, value)
    }

    /**
     * updates items by a stored proc
     */
    fun updateByProc(name: String, args: List<Any>? = null): Int {
        return repoT().updateByProc(name, args)
    }

    /**
     * updates items using the query
     */
    fun updateByQuery(query: IQuery): Int {
        return repoT().updateByQuery(query)
    }
}
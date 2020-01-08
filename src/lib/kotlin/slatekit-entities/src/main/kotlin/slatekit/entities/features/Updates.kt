package slatekit.entities.features

import kotlin.reflect.KProperty
import slatekit.common.DateTime
import slatekit.entities.Entity
import slatekit.entities.events.EntityAction
import slatekit.entities.events.EntityEvent
import slatekit.entities.core.ServiceSupport
import slatekit.entities.events.EntityHooks
import slatekit.entities.slatekit.entities.EntityOptions
import slatekit.meta.Reflector
import slatekit.meta.kClass
import slatekit.query.IQuery
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Updates<TId, T> : ServiceSupport<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    /**
     * directly modifies an entity without any additional processing/hooks/etc
     * @param entity
     * @return
     */
    fun modify(entity: T): Boolean {
        return repo().update(entity)
    }

    /**
     * directly modifies an entity without any additional processing/hooks/etc
     * @param entity
     * @return
     */
    fun patch(id:TId, values:List<Pair<String,Any?>>): Int {
        return repo().patch(id, values)
    }

    /**
     * creates the entity in the data store with additional processing based on the options supplied
     * @param entity : The entity to save
     * @param options: Settings to determine whether to apply metadata, and notify via Hooks
     */
    fun update(entity: T, options: EntityOptions): Pair<Boolean, T> {
        val useHooks = options.applyHooks && this is EntityHooks
        val original: T? = if (useHooks) repo().getById(entity.identity()) else null

        // Massage
        val entityFinal = when (options.applyMetadata) {
            true -> applyFieldData(EntityAction.Update, entity)
            false -> entity
        }

        // Update
        val success = modify(entityFinal)

        // Event out
        if (this is EntityHooks) {
            when (success) {
                true -> this.onEntityEvent(EntityEvent.EntityUpdated(original ?: entity, entityFinal, DateTime.now()))
                else -> this.onEntityEvent(EntityEvent.EntityErrored(entity,
                        Exception("unable to update: " + entity.toString()), DateTime.now()))
            }
        }
        return Pair(success, entityFinal)
    }

    /**
     * updates the entity in the data store and sends an event if there is support for Hooks
     * @param entity
     * @return
     */
    fun update(entity: T): Boolean {
        val original: T? = if (this is EntityHooks) repo().getById(entity.identity()) else null
        val finalEntity = applyFieldData(EntityAction.Update, entity)
        val success = repo().update(finalEntity)

        // Event out
        if (this is EntityHooks) {
            when (success) {
                true -> this.onEntityEvent(EntityEvent.EntityUpdated(original ?: entity, entity, DateTime.now()))
                else -> this.onEntityEvent(EntityEvent.EntityErrored(entity,
                        Exception("unable to update: " + entity.toString()), DateTime.now()))
            }
        }
        return success
    }

    /**
     * updates the entity in the data-store with error-handling
     * @param entity
     * @return
     */
    fun updateAsTry(entity: T): Try<Boolean> {
        return Tries.of { update(entity) }
    }

    /**
     * updates the entity field in the datastore
     * @param id: id of the entity
     * @param field: the name of the field
     * @param value: the value to set on the field
     * @return
     */
    fun update(id: TId, field: String, value: String) {
        val item = repo().getById(id)
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
    fun updateField(prop: KProperty<*>, value: Any): Int {
        return repo().updateField(prop.name, value)
    }

    /**
     * updates items based on the field name
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun updateByField(prop: KProperty<*>, oldValue: Any?, newValue:Any?): Int {
        return repo().updateByField(prop.name, oldValue, newValue)
    }

    /**
     * updates items by a stored proc
     */
    fun updateByProc(name: String, args: List<Any>? = null): Int {
        return repo().updateByProc(name, args)
    }

    /**
     * updates items using the query
     */
    fun updateByQuery(query: IQuery): Int {
        return repo().updateByQuery(query)
    }
}

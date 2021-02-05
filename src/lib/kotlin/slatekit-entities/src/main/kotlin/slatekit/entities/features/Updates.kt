package slatekit.entities.features

import slatekit.common.data.DataAction
import slatekit.query.Op
import slatekit.common.data.Value
import kotlin.reflect.KProperty
import slatekit.entities.Entity
import slatekit.entities.core.EntityOps
import slatekit.entities.EntityOptions
import slatekit.meta.Reflector
import slatekit.meta.kClass
import slatekit.query.Delete
import slatekit.query.Update
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Updates<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

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
    fun patch(id:TId, values:List<Value>): Int {
        return repo().patchById(id, values)
    }

    /**
     * creates the entity in the data store with additional processing based on the options supplied
     * @param entity : The entity to save
     * @param options: Settings to determine whether to apply metadata, and notify via Hooks
     */
    fun update(entity: T, options: EntityOptions): Pair<Boolean, T> {
        // Massage
        val entityFinal = when (options.applyMetadata) {
            true -> applyFieldData(DataAction.Update, entity)
            false -> entity
        }

        // Update
        val success = modify(entityFinal)
        return Pair(success, entityFinal)
    }

    /**
     * updates the entity in the data store and sends an event if there is support for Hooks
     * @param entity
     * @return
     */
    fun update(entity: T): Boolean {
        val finalEntity = applyFieldData(DataAction.Update, entity)
        val success = repo().update(finalEntity)
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
     * updates items using the query
     */
    fun updateByQuery(builder: Update): Int {
        return repo().patchByQuery(builder)
    }

    /**
     * updates items based on the field name
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun patchByField(prop: KProperty<*>, value: Any): Int {
        return repo().patchByField(prop.name, value)
    }

    /**
     * updates items based on the field name
     * @param prop: The property reference
     * @param value: The value to check for
     * @return
     */
    fun patchByFields(prop: KProperty<*>, oldValue: Any?, newValue:Any?): Int {
        return repo().patchByValue(prop.name, oldValue, newValue)
    }

    /**
     * updates items using the query
     */
    suspend fun patch(builder: Update.() -> Unit): Int {
        return repo().patch(builder)
    }


    fun update(): Update = repo().patch()
}

package slatekit.data.support

import slatekit.common.DateTimes
import slatekit.common.Prototyping
import slatekit.common.utils.ListMap
import slatekit.data.FullRepo
import slatekit.data.events.EntityAction
import slatekit.data.events.EntityEvent
import slatekit.data.events.EntityHooks
import slatekit.data.core.Meta
import slatekit.common.data.Filter
import slatekit.common.data.Logical
import slatekit.common.data.Value

/**
 * Used mostly for Prototyping and Testing.
 * @param meta: Provides information about id and table
 * @param idGen: AutoIncrementing Long for this in-memory table to generate ids
 * @param hooks: "Middleware" support to notify listeners for changes ( create, update, deletes )
 */
@Prototyping("NON-PRODUCTION USAGE: Used for prototyping, proof-of-concept, tests")
class InMemoryRepo<TId, T>(override val meta: Meta<TId, T>,
                           private val idGen: IdGenerator<TId>,
                           private val hooks: EntityHooks<TId, T>?) : FullRepo<TId, T> where TId : Comparable<TId>, T : Any {
    // Ordered list + map features
    private var items = ListMap<TId, T>(listOf())

    /**
     * Creates the entity only in memory in this repo
     */
    override fun create(entity: T): TId {
        return execute(entity) {
            when (isPersisted((entity))) {
                true -> identity(entity)
                false -> {
                    val id = idGen.nextId()
                    // store
                    items = items.add(id, entity)
                    notify(EntityAction.Create, entity)
                    id
                }
            }
        }
    }

    /**
     * Updates the entity only in memory in this repo
     */
    override fun update(entity: T): Boolean {
        val id = identity(entity)
        return execute(entity) {
            if (isPersisted(entity) && items.contains(id)) {
                items = items.minus(id)
                items = items.add(id, entity)
                notify(EntityAction.Update, entity)
                true
            } else false
        }
    }

    override fun getById(id: TId): T? {
        return items[id]
    }

    override fun getByIds(ids: List<TId>): List<T> {
        return ids.mapNotNull { id -> items.get(id) }
    }

    override fun getAll(): List<T> {
        return items.all()
    }

    override fun delete(entity: T?): Boolean {
        return entity?.let {
            val id = identity(it)
            val deleted = deleteById(id)
            deleted
        } ?: false
    }

    override fun deleteById(id: TId): Boolean {
        return when (!items.contains(id)) {
            false -> false
            true -> {
                val item = items.get(id)
                item?.let {
                    execute(it) {
                        items = items.remove(id)
                        item?.let { notify(EntityAction.Delete, item) }
                        true
                    }
                } ?: false
            }
        }
    }

    override fun deleteByIds(ids: List<TId>): Int {
        return ids.map { deleteById(it) }.count { it }
    }

    override fun deleteAll(): Long {
        val count = items.size
        items = ListMap(listOf())
        return count.toLong()
    }

    /**
     * Total number of items in this repo
     */
    override fun count(): Long {
        return items.size.toLong()
    }

    override fun seq(count: Int, desc: Boolean): List<T> {
        return when (desc) {
            false -> items.all().take(count)
            true -> items.all().takeLast(count)
        }
    }

    override fun deleteByFilters(filters: List<Filter>, logical: Logical): Int {
        TODO("This class for prototyping purposes only: Not yet implemented")
    }

    override fun patchByFilters(fields: List<Value>, filters: List<Filter>, logical: Logical): Int {
        TODO("This class for prototyping purposes only: Not yet implemented")
    }

    override fun findByFilters(filters: List<Filter>, logical: Logical): List<T> {
        TODO("This class for prototyping purposes only: Not yet implemented")
    }

    private fun notify(action: EntityAction, entity:T) {
        val id = identity(entity)
        val ts = DateTimes.now()
        when(action) {
            EntityAction.Create -> hooks?.onEntityEvent(EntityEvent.EntityCreated<TId, T>(name, id, entity, ts))
            EntityAction.Update -> hooks?.onEntityEvent(EntityEvent.EntityUpdated<TId, T>(name, id, entity, ts))
            EntityAction.Delete -> hooks?.onEntityEvent(EntityEvent.EntityDeleted<TId, T>(name, id, entity, ts))
            else -> { }
        }
    }

    private fun <A> execute(t:T, op:() -> A): A {
        return try {
            op()
        }
        catch(ex:Exception) {
            val id = identity(t)
            hooks?.onEntityEvent(EntityEvent.EntityErrored(meta.name, id, t, ex, DateTimes.now()))
            throw ex
        }
    }
}

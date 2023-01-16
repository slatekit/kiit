package kiit.data.support

import slatekit.common.DateTimes
import slatekit.common.Prototyping
import slatekit.common.data.DataAction
import slatekit.common.values.ListMap
import kiit.data.FullRepo
import slatekit.common.data.DataEvent
import slatekit.common.data.DataHooks
import slatekit.common.data.Values
import kiit.data.BaseRepo
import kiit.data.core.Meta
import kiit.data.sql.Dialect
import kiit.query.Select
import kiit.query.Update
import kiit.query.Delete
import kiit.query.Order

/**
 * Used mostly for Prototyping and Testing.
 * @param meta: Provides information about id and table
 * @param idGen: AutoIncrementing Long for this in-memory table to generate ids
 * @param hooks: "Middleware" support to notify listeners for changes ( create, update, deletes )
 */
@Prototyping("NON-PRODUCTION USAGE: Used for prototyping, proof-of-concept, tests")
class InMemoryRepo<TId, T>(meta: Meta<TId, T>,
                           private val idGen: IdGenerator<TId>,
                           hooks: DataHooks<TId, T>?) : BaseRepo<TId, T>(meta, hooks), FullRepo<TId, T> where TId : Comparable<TId>, T : Any {
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
                    val success = isPersisted(id)
                    notify(DataAction.Create, id, entity, success)
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
                val success = true
                notify(DataAction.Update, id, entity, success)
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
                        item?.let { notify(DataAction.Delete, id, item, true) }
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

    override fun seq(count: Int, order: Order): List<T> {
        return when (order) {
            Order.Asc -> items.all().take(count)
            Order.Dsc -> items.all().takeLast(count)
        }
    }

    private fun <A> execute(t:T, op:() -> A): A {
        return try {
            op()
        }
        catch(ex:Exception) {
            val id = identity(t)
            hooks?.onDataEvent(DataEvent.DataErrored(meta.name, id, t, ex, DateTimes.now()))
            throw ex
        }
    }

    override fun deleteByQuery(builder: Delete): Int {
        TODO("Not yet implemented")
    }

    override fun findByQuery(builder: Select): List<T> {
        TODO("Not yet implemented")
    }

    override fun patchByQuery(builder: Update): Int {
        TODO("Not yet implemented")
    }

    override fun delete(): Delete {
        TODO("Not yet implemented")
    }

    override fun select(): Select {
        TODO("Not yet implemented")
    }

    override fun patch(): Update {
        TODO("Not yet implemented")
    }

    override val dialect: Dialect
        get() = TODO("Not yet implemented")

    override fun scalar(sql: String, args: Values): Double {
        TODO("Not yet implemented")
    }

    override fun scalar(builder: Select.() -> Unit): Double {
        TODO("Not yet implemented")
    }
}

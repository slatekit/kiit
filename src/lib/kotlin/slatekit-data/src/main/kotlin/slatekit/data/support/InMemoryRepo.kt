package slatekit.data.support

import slatekit.common.Prototyping
import slatekit.common.utils.ListMap
import slatekit.data.SqlRepo
import slatekit.data.core.EntityUpdatable
import slatekit.query.Op

/**
 * Used mostly for Prototyping and Testing.
 */
@Prototyping("NON-PRODUCTION USAGE: Used for prototyping, proof-of-concept, tests")
class InMemoryRepo<TId, T>(private val pk:String,
                           private val tableName:String,
                           private val op:(T)-> TId,
                           private val idGen:IdGenerator<TId> ) : SqlRepo<TId, T> where TId: Number, TId : Comparable<TId>, T:Any {
    // Ordered list + map features
    private var items = ListMap<TId, T>(listOf())

    /**
     * Name of the Id/Primary key
     */
    override fun id(): String {
        return pk
    }

    /**
     * Name of this repo
     */
    override fun name(): String {
        return tableName
    }

    /**
     * Whether item is persisted already
     */
    override fun isPersisted(entity: T): Boolean {
        val id = identity(entity)
        return id.toLong() > 0
    }

    /**
     * Gets the identity of the entity ( primary key value )
     */
    override fun identity(entity: T): TId {
        return op(entity)
    }

    /**
     * Total number of items in this repo
     */
    override fun count(): Long {
        return items.size.toLong()
    }

    /**
     * Creates the entity only in memory in this repo
     */
    override fun create(entity: T): TId {
        return when(isPersisted((entity))) {
            true -> identity(entity)
            false -> {
                val id = idGen.nextId()
                val en = when (entity) {
                    is EntityUpdatable<*, *> -> entity.withIdAny(id) as T
                    else -> entity
                }
                // store
                items = items.add(id, en as T)
                id
            }
        }
    }

    /**
     * Updates the entity only in memory in this repo
     */
    override fun update(entity: T): Boolean {
        val id = identity(entity)
        return if (isPersisted(entity) && items.contains(id)) {
            items = items.minus(id)
            items = items.add(id, entity)
            true
        }
        else false
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
            deleteById(id)
        } ?: false
    }

    override fun deleteById(id: TId): Boolean {
        return when(!items.contains(id)) {
            false -> false
            true -> { items = items.remove(id); true }
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

    override fun seq(count: Int, desc: Boolean): List<T> {
        return when(desc) {
            false -> items.all().take(count)
            true  -> items.all().takeLast(count)
        }
    }

    override fun deleteByFields(conditions: List<Triple<String, Op, Any?>>): Int {
        TODO("This class for prototyping purposes only: Not yet implemented")
    }

    override fun patchByFields(fields: List<Pair<String, Any?>>, conditions: List<Triple<String, Op, Any?>>): Int {
        TODO("This class for prototyping purposes only: Not yet implemented")
    }

    override fun findByFields(conditions: List<Triple<String, Op, Any>>): List<T> {
        TODO("This class for prototyping purposes only: Not yet implemented")
    }

    override fun findOneByField(field: String, op: Op, value: Any): T? {
        TODO("This class for prototyping purposes only: Not yet implemented")
    }
}

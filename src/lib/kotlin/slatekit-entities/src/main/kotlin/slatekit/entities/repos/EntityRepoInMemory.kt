/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.entities.repos

import slatekit.common.DateTime
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.common.utils.ListMap
import slatekit.query.IQuery
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityMapper
import slatekit.entities.core.EntityRepo
import slatekit.entities.core.EntityUpdatable
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import java.util.*
//import java.time.*
import org.threeten.bp.*
import slatekit.common.Record
import slatekit.meta.models.Model
import kotlin.reflect.KClass


open class EntityRepoInMemoryWithLongId<T>(cls:KClass<T>, idGen:IdGenerator<Long>)
    : EntityRepoInMemory<Long, T>(cls, Long::class, idGenerator = idGen)
        where T:Entity<Long> {

    companion object {
        @JvmStatic operator fun <T> invoke(cls:KClass<T>):EntityRepoInMemoryWithLongId<T> where T:Entity<Long> {
            val idGen = LongIdGenerator()
            return EntityRepoInMemoryWithLongId(cls, idGen)
        }
    }
}


open class EntityRepoInMemoryWithIntId<T>(cls:KClass<T>, idGen:IdGenerator<Int>)
    : EntityRepoInMemory<Int, T>(cls, Int::class, idGenerator = idGen )
        where T:Entity<Int> {

    companion object {

        @JvmStatic operator fun <T> invoke(cls:KClass<T>):EntityRepoInMemoryWithIntId<T> where T:Entity<Int> {
            val idGen = IntIdGenerator()
            return EntityRepoInMemoryWithIntId(cls, idGen)
        }
    }
}



/**
 * An In-Memory repository to store entities.
 * Used as an alternative to mocking for testing/prototyping.
 *
 * WARNING!!!!!!
 * Should NOT be used outside of prototyping
 */
open class EntityRepoInMemory<TId, T>(
    entityType: KClass<*>,
    entityIdType: KClass<*>,
    encryptor: Encryptor? = null,
    namer: Namer? = null,
    idGenerator: IdGenerator<TId>? = null
)
    : EntityRepo<TId, T>(entityType, entityIdType, entityType.simpleName ?: "", encryptor = encryptor, namer = namer)
        where TId: kotlin.Comparable<TId>, T:Entity<TId> {

    private val idGenerator = idGenerator ?: LongIdGenerator()
    private var items = ListMap<TId, T>(listOf())

    /**
     * create the entity in memory
     *
     * @param entity
     */
    override fun create(entity: T): TId {
        // Check 1: already persisted ?
        return if (!entity.isPersisted()) {
            // get next id
            val id = getNextId()
            val en = when (entity) {
                is EntityUpdatable<*, *> -> entity.withIdAny(id)
                else -> { }
            }

            // store
            items = items.add(id, en as T)
            id
        } else
            entity.identity()
    }

    /**
     * updates the entity in memory
     *
     * @param entity
     */
    override fun update(entity: T): Boolean {
        // Check 1: already persisted ?
        if (entity.isPersisted() && items.contains(entity.identity())) {
            items = items.minus(entity.identity())
            items = items.add(entity.identity(), entity)
        }
        return true
    }

    /**
     * deletes the entity in memory
     *
     * @param id
     */
    override fun delete(id: TId): Boolean {
        return if (!items.contains(id))
            false
        else {
            items = items.remove(id)
            true
        }
    }

    /**
     * deletes all entities from the datastore using the ids
     * @param ids
     * @return
     */
    override fun delete(ids: List<TId>): Int {
        return ids.map({ delete(it) }).count { it }
    }

    /**
     * deletes all entities from the data store using the ids
     * @param ids
     * @return
     */
    override fun deleteAll(): Long {
        val count = items.size
        items = ListMap(listOf())
        return count.toLong()
    }

    /**
     * gets the entity from memory with the specified id.
     *
     * @param id
     */
    override fun get(id: TId): T? {
        return items[id]
    }

    /**
     * gets all the entities using the supplied ids
     * @param ids
     * @return
     */
    override fun get(ids: List<TId>): List<T> {
        return ids.mapNotNull { items[it] }
    }

    /**
     * finds items based on the query
     * @param query
     * @return
     */
    override fun findBy(field: String, op: String, value: Any): List<T> {
        val propRaw  = Reflector.findPropertyExtended(entityType, field)
        //val prop =  if(propRaw != null) propRaw else Reflector.findField(entityType)
        val prop = propRaw
        val matched = prop?.let { property ->
            val cls = KTypes.getClassFromType(property.returnType)

            val finalValue = if (value is UUID) value.toString() else value
            property.let { p ->
                items.all().filter { it ->
                    val actual = Reflector.getFieldValue(it, p)
                    val expected = finalValue
                    compare(cls, actual, expected)
                }
            }
        } ?: listOf()
        return matched
    }

    /**
     * finds items based on the query
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    override fun findFirstBy(field: String, op: String, value: Any): T? {
        return findBy(field, op, value).firstOrNull()
    }

    /**
     * gets all the items from memory
     *
     * @return
     */
    override fun getAll(): List<T> = items.all()

    override fun count(): Long = items.size.toLong()

    override fun top(count: Int, desc: Boolean): List<T> {
        return if (items.size == 0) {
            listOf()
        } else {
            val items = items.all().sortedBy { item -> item.identity() }
            val sorted = if (desc) items.reversed() else items
            sorted.take(count)
        }
    }

    override fun updateByField(field: String, value: Any): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun updateByProc(name: String, args: List<Any>?): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun updateByQuery(query: IQuery): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteByField(field: String, value: Any): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteByQuery(query: IQuery): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }


    override fun count(query: IQuery): Long {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    fun getNextId(): TId = idGenerator.nextId() as TId

    /**
     * Simple equality comparison fields ( used mostly in
     * testing / prototyping of models via this In-Memory Repository.
     */
    fun compare(cls: KClass<*>, a: Any?, b: Any): Boolean {
        return a?.let { actual ->
            when (cls) {
                KTypes.KStringClass -> (actual as String) == (b as String)
                KTypes.KBoolClass -> (actual as Boolean) == (b as Boolean)
                KTypes.KShortClass -> (actual as Short) == (b as Short)
                KTypes.KIntClass -> (actual as Int) == (b as Int)
                KTypes.KLongClass -> (actual as Long) == (b as Long)
                KTypes.KFloatClass -> (actual as Float) == (b as Float)
                KTypes.KDoubleClass -> (actual as Double) == (b as Double)
                KTypes.KLocalDateClass -> (actual as LocalDate) == (b as LocalDate)
                KTypes.KLocalTimeClass -> (actual as LocalTime) == (b as LocalTime)
                KTypes.KLocalDateTimeClass -> (actual as LocalDateTime) == (b as LocalDateTime)
                KTypes.KZonedDateTimeClass -> (actual as ZonedDateTime) == (b as ZonedDateTime)
                KTypes.KDateTimeClass -> (actual as DateTime) == (b as DateTime)
                else -> false
            }
        } ?: false
    }
}


class EntityMapperEmpty<TId, T>(val model:Model?)
    : EntityMapper<TId, T> where TId:Comparable<TId>, T:Entity<TId> {

    override fun schema(): Model? = model

    override fun <T> mapFrom(record: Record): T? = null
}

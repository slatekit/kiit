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
import slatekit.common.utils.ListMap
import slatekit.query.IQuery
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import java.util.*
// import java.time.*
import org.threeten.bp.*
import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.Values
import slatekit.entities.*
import slatekit.entities.core.EntityInfo
import slatekit.meta.models.Model
import slatekit.query.Op
import kotlin.reflect.KClass

open class InMemoryRepoWithLongId<T>(info: EntityInfo, idGen: IdGenerator<Long>) :
    InMemoryRepo<Long, T>(info, idGen) where T: Any {

    constructor(cls: KClass<T>) :
        this(EntityInfo.memory(Long::class, cls), LongIdGenerator())
}

/**
 * An In-Memory repository to store entities.
 * Used as an alternative to mocking for testing/prototyping.
 *
 * WARNING!!!!!!
 * Should NOT be used outside of prototyping
 */
open class InMemoryRepo<TId, T>(
    info: EntityInfo,
    val idGenerator: IdGenerator<TId>
) : BaseRepo<TId, T>(info)
        where TId : kotlin.Comparable<TId>, T: Any {

    protected var items = ListMap<TId, T>(listOf())

    /**
     * create the entity in memory
     *
     * @param entity
     */
    override fun create(entity: T): TId {
        // Check 1: already persisted ?
        return if (!isPersisted(entity)) {
            // get next id
            val id = getNextId()
            val en = when (entity) {
                is EntityUpdatable<*, *> -> entity.withIdAny(id)
                else -> entity
            }

            // store
            items = items.add(id, en as T)
            id
        } else
            identity(entity)
    }

    /**
     * updates the entity in memory
     *
     * @param entity
     */
    override fun update(entity: T): Boolean {
        // Check 1: already persisted ?
        if (isPersisted(entity) && items.contains(identity(entity))) {
            items = items.minus(identity(entity))
            items = items.add(identity(entity), entity)
        }
        return true
    }

    /**
     * deletes the entity in memory
     *
     * @param id
     */
    override fun deleteById(id: TId): Boolean {
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
    override fun deleteByIds(ids: List<TId>): Int {
        return ids.map({ deleteById(it) }).count { it }
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
    override fun getById(id: TId): T? {
        return items[id]
    }

    /**
     * gets all the entities using the supplied ids
     * @param ids
     * @return
     */
    override fun getByIds(ids: List<TId>): List<T> {
        val items = ids.map {
            val item = items[it]
            item
        }
        return items.filterNotNull()
    }

    /**
     * finds items based on the query
     * @param query
     * @return
     */
    override fun findByField(fieldRaw: String, op: Op, value: Any): List<T> {
        return filter(items.all(), fieldRaw, op.text, value)
    }

    /**
     * finds items based on the conditions
     */
    override fun findByFields(conditions: List<Pair<String, Any>>): List<T> {
        val all = items.all()
        val filtered = conditions.fold(all) { items, condition ->
            val matches = filter(items, condition.first, "=", condition.second)
            matches
        }
        return filtered
    }

    /**
     * finds items based on the query
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    override fun findOneByField(field: String, op: Op, value: Any): T? {
        return findByField(field, op, value).firstOrNull()
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
            val items = items.all().sortedBy { item -> identity(item) }
            val sorted = if (desc) items.reversed() else items
            sorted.take(count)
        }
    }

    override fun patchByFields(fields: List<Pair<String, Any?>>, conditions: List<Pair<String, Any?>>): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun updateByProc(name: String, args: List<Any>?): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun updateByQuery(query: IQuery): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteByField(field: String, op: Op, value: Any): Int {
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

    protected fun filter(all: List<T>, fieldRaw: String, op: String, value: Any): List<T> {
        val field = info.model?.fields?.find { it.storedName.toLowerCase() == fieldRaw.toLowerCase() }
        val prop = field?.prop ?: Reflector.findPropertyExtended(info.modelType, field?.name ?: fieldRaw)
        val matched = prop?.let { property ->
            val cls = KTypes.getClassFromType(property.returnType)

            val finalValue = if (value is UUID) value.toString() else value
            property.let { p ->
                all.filter { it ->
                    val actual = Reflector.getFieldValue(it as Any, p)
                    val expected = finalValue
                    compare(cls, actual, expected)
                }
            }
        } ?: listOf()
        return matched
    }


    companion object {

        inline fun <reified TId, reified T> of():InMemoryRepo<TId, T> where TId : Comparable<TId>, T:Any {
            val idGen = if(TId::class == Int::class) IntIdGenerator() else LongIdGenerator()
            val repo = InMemoryRepo<TId, T>(EntityInfo.memory(TId::class, T::class), idGen as IdGenerator<TId>)
            return repo
        }
    }
}

class EntityMapperEmpty<TId, T>(val model: Model?) :
    EntityMapper<TId, T> where TId : Comparable<TId>, T : Entity<TId> {

    override fun encode(model: T, action:DataAction, encryptor: Encryptor?): Values {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun schema(): Model? = model

    override fun decode(record: Record, encryptor: Encryptor?): T? = null
}

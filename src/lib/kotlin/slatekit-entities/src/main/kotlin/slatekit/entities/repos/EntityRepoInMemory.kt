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
import slatekit.common.ListMap
import slatekit.common.query.IQuery
import slatekit.common.query.Query
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityMapper
import slatekit.entities.core.EntityRepo
import slatekit.entities.core.EntityUpdatable
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.reflect.KClass


open class EntityRepoInMemory<T>(
        entityType: KClass<*>,
        entityIdType: KClass<*>? = null,
        entityMapper: EntityMapper? = null
)
    : EntityRepo<T>(entityType, entityIdType, entityMapper, null) where T : Entity {

    private var _items = ListMap<Long, T>(listOf())


    /**
     * create the entity in memory
     *
     * @param entity
     */
    override fun create(entity: T): Long {
        // Check 1: already persisted ?
        return if (!entity.isPersisted()) {
            // get next id
            val id = getNextId()
            val en = when (entity) {
                is EntityUpdatable<*> -> entity.withId(id)
                else                  -> _entityMapper.copyWithId(id, entity)
            }

            // store
            _items = _items.add(id, en as T)
            id
        }
        else
            entity.identity()
    }


    /**
     * updates the entity in memory
     *
     * @param entity
     */
    override fun update(entity: T): T {
        // Check 1: already persisted ?
        if (entity.isPersisted() && _items.contains(entity.identity())) {
            _items = _items.minus(entity.identity())
            _items = _items.add(entity.identity(), entity)
        }
        return entity
    }


    /**
     * deletes the entity in memory
     *
     * @param id
     */
    override fun delete(id: Long): Boolean =
            if (!_items.contains(id))
                false
            else {
                _items = _items.remove(id)
                true
            }


    /**
     * gets the entity from memory with the specified id.
     *
     * @param id
     */
    override fun get(id: Long): T? {
        return _items[id]
    }


    /**
     * finds items based on the query
     * @param query
     * @return
     */
    override fun findBy(field: String, op: String, value: Any): List<T> {
        val prop = Reflector.findProperty(_entityType, field)
        val matched = prop?.let { property ->
            val cls = KTypes.getClassFromType(property.returnType)

            property.let { p ->
                _items.all().filter { it ->
                    val actual = Reflector.getFieldValue(it, p)
                    val expected = value
                    compare(cls, actual, expected)
                }
            }
        } ?: listOf()
        return matched
    }


    /**
     * finds items based on the query
     * @param field: name of field
     * @param op   : operator e.g. "="
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
    override fun getAll(): List<T> = _items.all()


    override fun count(): Long = _items.size.toLong()


    override fun top(count: Int, desc: Boolean): List<T> {
        return if (_items.size == 0) {
            listOf()
        }
        else {
            val items = _items.all().sortedBy { item -> item.identity() }
            val sorted = if (desc) items.reversed() else items
            sorted.take(count)
        }
    }


    override fun updateByField(field: String, value: Any): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateByProc(name: String, args: List<Any>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateByQuery(query: IQuery): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteByField(field: String, value: Any): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteByQuery(query: IQuery): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun getNextId(): Long = _items.size.toLong() + 1


    /**
     * Simple equality comparison fields ( used mostly in
     * testing / prototyping of models via this In-Memory Repository.
     */
    fun compare(cls: KClass<*>, a: Any?, b: Any): Boolean {
        return a?.let { actual ->
            when (cls) {
                KTypes.KStringClass          -> (actual as String) == (b as String)
                KTypes.KBoolClass            -> (actual as Boolean) == (b as Boolean)
                KTypes.KShortClass           -> (actual as Short) == (b as Short)
                KTypes.KIntClass             -> (actual as Int) == (b as Int)
                KTypes.KLongClass            -> (actual as Long) == (b as Long)
                KTypes.KFloatClass           -> (actual as Float) == (b as Float)
                KTypes.KDoubleClass          -> (actual as Double) == (b as Double)
                KTypes.KLocalDateClass       -> (actual as LocalDate) == (b as LocalDate)
                KTypes.KLocalTimeClass       -> (actual as LocalTime) == (b as LocalTime)
                KTypes.KLocalDateTimeClass   -> (actual as LocalDateTime) == (b as LocalDateTime)
                KTypes.KZonedDateTimeClass   -> (actual as ZonedDateTime) == (b as ZonedDateTime)
                KTypes.KDateTimeClass        -> (actual as DateTime) == (b as DateTime)
                else                       -> false
            }
        } ?: false
    }
}

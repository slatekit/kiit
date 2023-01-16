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

package kiit.integration.common

import kiit.apis.Action
import kiit.common.Ignore
import kiit.entities.Entities
import kiit.entities.Entity
import kiit.entities.EntityService
import kiit.entities.EntityUpdatable
import kiit.query.Op
import java.util.*
import kotlin.reflect.KClass

interface ApiWithEntitySupport<TId, T, TSvc>
        where TId:Comparable<TId>, T : Entity<TId>,
              TSvc : EntityService<TId, T> {

    val entityIdType:KClass<*>
    val entityType: KClass<*>
    val entities: Entities
    val entitySvc: EntityService<TId, T>

    @Action(name = "", desc = "gets the first item")
    suspend fun getById(id: TId): T? {
        return entitySvc.getById(id)
    }

    @Action(name = "", desc = "gets the first item")
    suspend fun copy(id: TId): T? {
        val item = entitySvc.getById(id)
        return item?.let { model ->
            when (model) {
                is EntityUpdatable<*, *> -> createFrom(model as EntityUpdatable<TId, T>)
                else -> null
            }
        }
    }

    @Action(name = "", desc = "gets all items")
    suspend fun getAll(): List<T> {
        return entitySvc.getAll()
    }

    @Action(name = "", desc = "creates an item")
    suspend fun create(item: T): TId {
        return entitySvc.create(item)
    }

    @Action(name = "", desc = "updates an item")
    suspend fun update(item: T) {
        entitySvc.update(item)
    }

//    @Action(name = "", desc = "gets the total number of users")
//    fun total(): Long {
//        return entitySvc.count()
//    }
//
//    @Action(name = "", desc = "whether or not this dataset is empty")
//    fun isEmpty(): Boolean = total() == 0L
//
//    @Action(name = "", desc = "gets the first item")
//    fun first(): T? {
//        return entitySvc.first()
//    }
//
//    @Action(name = "", desc = "gets the last item")
//    fun last(): T? {
//        return entitySvc.last()
//    }
//
//    @Action(name = "", desc = "gets recent items in the system")
//    fun recent(count: Int = 5): List<T> {
//        return entitySvc.recent(count)
//    }
//
//    @Action(name = "", desc = "gets oldest items in the system")
//    fun oldest(count: Int = 5): List<T> {
//        return entitySvc.oldest(count)
//    }

    @Action(name = "", desc = "gets distinct items based on the field")
    suspend fun distinct(field: String): List<Any> {
        return listOf<Any>()
    }

    @Action(name = "", desc = "finds items by field name and value")
    suspend fun findBy(field: String, value: String): List<T> {
        return entitySvc.findByField(field, Op.Eq, value)
    }

    @Action(name = "", desc = "finds items by field name and value")
    suspend fun updateField(id: TId, field: String, value: String) {
        entitySvc.update(id, field, value)
    }

    @Action(name = "", desc = "deletes an item by its id")
    suspend fun delete(item: T): Boolean {
        return entitySvc.deleteById(item.identity())
    }

    @Action(name = "", desc = "deletes an item by its id")
    suspend fun deleteById(id: TId): Boolean {
        return entitySvc.deleteById(id)
    }

    @Ignore
    suspend fun createFrom(updatable: EntityUpdatable<TId, T>): T {
        val copy = updatable.withId(zeroId())
        val newId = entitySvc.create(copy)
        val final = updatable.withId(newId)
        return final
    }


    private fun zeroId():TId {
        return when(entityIdType) {
            Int::class -> 0 as TId
            Long::class -> 0L as TId
            UUID::class -> UUID.fromString("00000000-0000-0000-0000-000000000000") as TId
            String::class -> "" as TId
            else          -> throw Exception("Unexpected id type")
        }
    }
}

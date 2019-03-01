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

package slatekit.integration.common

import slatekit.apis.ApiAction
import slatekit.common.Ignore
import slatekit.query.Query
import slatekit.entities.core.Entities
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityService
import slatekit.entities.core.EntityUpdatable
import java.util.*
import kotlin.reflect.KClass

interface ApiWithEntitySupport<TId, T, TSvc> where TId:Comparable<TId>, T : Entity<TId>, TSvc : EntityService<TId, T> {

    val entityIdType:KClass<*>
    val entityType: KClass<*>
    val entities: Entities<*>
    val entitySvc: EntityService<TId, T>

    @ApiAction(name = "", desc = "gets the total number of users", roles = "@parent", verb = "get", protocol = "@parent")
    fun total(): Long {
        return entitySvc.count()
    }

    @ApiAction(name = "", desc = "whether or not this dataset is empty", roles = "@parent", verb = "get", protocol = "@parent")
    fun isEmpty(): Boolean = total() == 0L

    @ApiAction(name = "", desc = "gets the first item", roles = "@parent", verb = "get", protocol = "@parent")
    fun getById(id: TId): T? {
        return entitySvc.get(id)
    }

    @ApiAction(name = "", desc = "gets the first item", roles = "@parent", verb = "get", protocol = "@parent")
    fun copy(id: TId): T? {
        val item = entitySvc.get(id)
        return item?.let { model ->
            when (model) {
                is EntityUpdatable<*,*> -> createFrom(model as EntityUpdatable<TId, T>)
                else -> null
            }
        }
    }

    @ApiAction(name = "", desc = "gets all items", roles = "@parent", verb = "get", protocol = "@parent")
    fun getAll(): List<T> {
        return entitySvc.getAll()
    }

    @ApiAction(name = "", desc = "creates an item", roles = "@parent", verb = "post", protocol = "@parent")
    fun create(item: T): TId {
        return entitySvc.create(item)
    }

    @ApiAction(name = "", desc = "updates an item", roles = "@parent", verb = "put", protocol = "@parent")
    fun update(item: T) {
        entitySvc.update(item)
    }

    @ApiAction(name = "", desc = "gets the first item", roles = "@parent", verb = "get", protocol = "@parent")
    fun first(): T? {
        return entitySvc.first()
    }

    @ApiAction(name = "", desc = "gets the last item", roles = "@parent", verb = "get", protocol = "@parent")
    fun last(): T? {
        return entitySvc.last()
    }

    @ApiAction(name = "", desc = "gets recent items in the system", roles = "@parent", verb = "get", protocol = "@parent")
    fun recent(count: Int = 5): List<T> {
        return entitySvc.recent(count)
    }

    @ApiAction(name = "", desc = "gets oldest items in the system", roles = "@parent", verb = "get", protocol = "@parent")
    fun oldest(count: Int = 5): List<T> {
        return entitySvc.oldest(count)
    }

    @ApiAction(name = "", desc = "gets distinct items based on the field", roles = "@parent", verb = "get", protocol = "@parent")
    fun distinct(field: String): List<Any> {
        return listOf<Any>()
    }

    @ApiAction(name = "", desc = "finds items by field name and value", roles = "@parent", verb = "get", protocol = "@parent")
    fun findBy(field: String, value: String): List<T> {
        return entitySvc.find(Query().where(field, "=", value))
    }

    @ApiAction(name = "", desc = "finds items by field name and value", roles = "@parent", verb = "post", protocol = "@parent")
    fun updateField(id: TId, field: String, value: String) {
        entitySvc.update(id, field, value)
    }

    @ApiAction(name = "", desc = "deletes an item by its id", roles = "@parent", verb = "delete", protocol = "@parent")
    fun delete(item: T): Boolean {
        return entitySvc.deleteById(item.identity())
    }

    @ApiAction(name = "", desc = "deletes an item by its id", roles = "@parent", verb = "delete", protocol = "@parent")
    fun deleteById(id: TId): Boolean {
        return entitySvc.deleteById(id)
    }

    @Ignore
    fun createFrom(updatable: EntityUpdatable<TId, T>): T {
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

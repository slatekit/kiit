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

package slatekit.apis

import slatekit.common.query.Query
import slatekit.core.common.AppContext
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityService
import slatekit.entities.core.EntityUpdatable
import kotlin.reflect.KClass


/**
 * Base class for an Api that is used to access/manage database models / entities using the
 * Slate Kit Orm ( Entities ).
 * @tparam T
 */
open class ApiBaseEntity<T>(context: AppContext, tpe: KClass<*>) : ApiBase(context) where T : Entity {

    protected val _service: EntityService<T> = context.ent.getSvc(tpe)


    @ApiAction(name = "", desc = "gets the total number of users", roles = "@parent", verb = "get", protocol = "@parent")
    fun total(): Long {
        return _service.count()
    }


    @ApiAction(name = "", desc = "gets the first item", roles = "@parent", verb = "get", protocol = "@parent")
    fun getById(id: Long): T? {
        return _service.get(id)
    }


    @ApiAction(name = "", desc = "gets the first item", roles = "@parent", verb = "get", protocol = "@parent")
    fun copy(id: Long): T? {
        val item = _service.get(id)
        return item?.let { model ->
            when (model) {
                is EntityUpdatable<*> -> create(model as EntityUpdatable<T>)
                else                  -> null
            }
        }
    }


    @ApiAction(name = "", desc = "gets all items", roles = "@parent", verb = "get", protocol = "@parent")
    fun getAll(): List<T> {
        return _service.getAll()
    }


    @ApiAction(name = "", desc = "gets the first item", roles = "@parent", verb = "get", protocol = "@parent")
    fun first(): T? {
        return _service.first()
    }


    @ApiAction(name = "", desc = "gets the last item", roles = "@parent", verb = "get", protocol = "@parent")
    fun last(): T? {
        return _service.last()
    }


    @ApiAction(name = "", desc = "gets recent items in the system", roles = "@parent", verb = "get", protocol = "@parent")
    fun recent(count: Int = 5): List<T> {
        return _service.recent(count)
    }


    @ApiAction(name = "", desc = "gets oldest items in the system", roles = "@parent", verb = "get", protocol = "@parent")
    fun oldest(count: Int = 5): List<T> {
        return _service.oldest(count)
    }


    @ApiAction(name = "", desc = "gets distinct items based on the field", roles = "@parent", verb = "get", protocol = "@parent")
    fun distinct(field: String): List<Any> {
        return listOf<Any>()
    }


    @ApiAction(name = "", desc = "finds items by field name and value", roles = "@parent", verb = "get", protocol = "@parent")
    fun findBy(field: String, value: String): List<T> {
        return _service.find(Query().where(field, "=", value))
    }


    @ApiAction(name = "", desc = "finds items by field name and value", roles = "@parent", verb = "post", protocol = "@parent")
    fun updateField(id: Long, field: String, value: String): Unit {
        _service.update(id, field, value)
    }


    @ApiAction(name = "", desc = "deletes an item by its id", roles = "@parent", verb = "delete", protocol = "@parent")
    fun delete(id: Long): Boolean {
        return _service.delete(id)
    }


    fun create(updatable: EntityUpdatable<T>): T {
        val copy = updatable.withId(0)
        val newId = _service.create(copy)
        val final = updatable.withId(newId)
        return final
    }
}

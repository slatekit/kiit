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

import slatekit.common.db.IDb
import slatekit.common.ext.tail
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query
import slatekit.entities.Entity
import slatekit.entities.EntityMapper
import slatekit.entities.core.EntityInfo

/**
 *
 * @param db   : Db wrapper to execute sql
 * @param info : Holds all info relevant state/members needed to perform repo operations
 * @tparam T
 */
abstract class EntityRepoSql<TId, T>(
        val db: IDb,
        info:EntityInfo,
        val mapper: EntityMapper<TId, T>
) : EntityRepoBase<TId, T>(info)
        where TId:Comparable<TId>, T: Entity<TId> {


    override fun name(): String = "${info.encodedChar}" + super.name() + "${info.encodedChar}"


    /**
     * updates the table field using the value supplied
     * @param field: The field name
     * @param value: The value to set
     */
    override fun updateByField(field: String, value: Any): Int {
        val query = Query().set(field, value)
        val updateSql = query.toUpdatesText()
        val sql = "update " + name() + updateSql
        return update(sql)
    }

    /**
     * updates items using the query
     * @param query: The query builder
     */
    override fun updateByQuery(query: IQuery): Int {
        val updateSql = query.toUpdatesText()
        val sql = "update " + name() + updateSql
        return update(sql)
    }

    /**
     * deletes the entity in memory
     *
     * @param id
     */
    override fun delete(id: TId): Boolean {
        val count = update("delete from ${name()} where ${id()} = $id;")
        return count > 0
    }

    /**
     * deletes all entities from the datastore using the ids
     * @param ids
     * @return
     */
    override fun delete(ids: List<TId>): Int {
        val delimited = ids.joinToString(",")
        return update("delete from ${name()} where ${id()} in ($delimited);")
    }

    /**
     * deletes all entities from the data store using the ids
     * @param ids
     * @return
     */
    override fun deleteAll(): Long {
        val count = update("delete from ${name()};")
        return count.toLong()
    }

    /**
     * deletes items based on the field name and value
     * @param field: The field name
     * @param op: The operation for the filter
     * @param value: The value to check for
     * @return
     */
    override fun deleteByField(field: String, op:Op, value: Any): Int {
        val query = Query().where(field, op, value)
        val filter = query.toFilter()
        val sql = "delete from " + name() + " where " + filter
        return update(sql)
    }

    /**
     * deletes items using the query
     * @param query: The query builder
     * @return
     */
    override fun deleteByQuery(query: IQuery): Int {
        val filter = query.toFilter()
        val sql = "delete from " + name() + " where " + filter
        return update(sql)
    }

    /**
     * gets the entity associated with the id
     */
    override fun get(id: TId): T? {
        return sqlMapOne("select * from ${name()} where ${id()} = $id;")
    }

    /**
     * gets all the entities using the supplied ids
     * @param ids
     * @return
     */
    override fun get(ids: List<TId>): List<T> {
        val delimited = ids.joinToString(",")
        return sqlMapMany("select * from ${name()} where ${id()} in ($delimited);") ?: listOf()
    }

    override fun getAll(): List<T> {
        val result = sqlMapMany("select * from ${name()};")
        return result ?: listOf<T>()
    }

    override fun count(): Long {
        val count = getScalarLong("select count(*) from ${name()};")
        return count
    }

    override fun top(count: Int, desc: Boolean): List<T> {
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        val sql = "select * from " + name() + orderBy + " limit " + count
        val items = sqlMapMany(sql) ?: listOf<T>()
        return items
    }


    /**
     * Gets the total number of records based on the query provided.
     */
    override fun count(query: IQuery):Long {
        val filter = query.toFilter()
        val sql = "select count( * ) from ${name()} where " + filter
        val count = getScalarLong(sql)
        return count
    }

    override fun find(query: IQuery): List<T> {
        val filter = query.toFilter()
        val sql = "select * from ${name()} where " + filter
        val results = sqlMapMany(sql)
        return results ?: listOf()
    }

    /**
     * finds items based on the query
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    override fun findBy(field: String, op: String, value: Any): List<T> {
        return find(Query().where(field, op, value))
    }

    /**
     * finds items based on the conditions
     */
    override fun findByFields(conditions:List<Pair<String, Any>>): List<T> {
        val first = conditions.first()
        val tail = conditions.tail()
        val query = Query().where(first.first, Op.Eq, first.second)
        tail.forEach {
            query.and(it.first, Op.Eq, it.second)
        }
        return find(query)
    }


    /**
     * finds items based on the field in the values provided
     * @param field: name of field
     * @param value: values of field to search against
     * @return
     */
    override fun findIn(field: String, value: List<Any>): List<T> {
        return find(Query().where(field, Op.In.text, value))
    }

    /**
     * finds items based on the query
     * @param field: name of field
     * @param op : operator e.g. "="
     * @param value: value of field to search against
     * @return
     */
    override fun findFirstBy(field: String, op: String, value: Any): T? {
        return find(Query().where(field, op, value)).firstOrNull()
    }

    /**
     * finds first item based on the query
     * @param query: name of field
     * @return
     */
    override fun findFirst(query:IQuery): T? {
        return find(query.limit(1)).firstOrNull()
    }

    /**
     * finds items by using the sql
     * @param query
     * @return
     */
    override fun findByProc(name: String, args: List<Any>?): List<T>? {
        return db.callQueryMapped(name, mapper, args)
    }

    /**
     * Updates records using sql provided and returns the number of updates made
     */
    protected open fun update(sql: String): Int {
        val count = db.update(sql)
        return count
    }

    /**
     * updates items using the proc and args
     */
    override fun updateByProc(name: String, args: List<Any>?): Int {
        return db.callUpdate(name, args)
    }

    protected open fun getScalarLong(sql:String):Long {
        return db.getScalarLong("select count(*) from ${name()};", null)
    }

    protected open fun sqlMapMany(sql: String): List<T>? {
        return db.mapMany(sql, mapper)
    }

    protected open fun sqlMapOne(sql: String): T? {
        return db.mapOne<T>(sql, mapper)
    }
}

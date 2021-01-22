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

package slatekit.entities

import slatekit.common.data.IDb
import slatekit.common.ext.tail
import slatekit.common.data.Mapper
import slatekit.data.features.Countable
import slatekit.data.features.Orderable
import slatekit.entities.EntityRepo
import slatekit.entities.core.EntityInfo
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query

/**
 *
 * @param db : Db wrapper to execute sql
 * @param info : Holds all info relevant state/members needed to perform repo operations
 * @tparam T
 */
open class EntitySqlRepo<TId, T>(
    val db: IDb,
    override val info: EntityInfo,
    val mapper: Mapper<TId, T>
) : EntityRepo<TId, T>, Countable<TId, T>, Orderable<TId, T> where TId : Comparable<TId> {

    override fun isPersisted(entity: T): Boolean {
        return info.idInfo.isPersisted(entity)
    }

    override fun identity(entity: T): TId {
        return info.idInfo.identity(entity)
    }

    override fun create(entity: T): TId {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(entity: T): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun name(): String = "${info.encodedChar}" + super.name() + "${info.encodedChar}"


    override fun patchByFields(fields: List<Pair<String, Any?>>, conditions: List<Triple<String, Op, Any?>>): Int {
        val query = Query()
        fields.forEach { query.set(it.first, it.second) }
        conditions.forEach { query.where(it.first, Op.Eq, it.second) }
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
     * Delete item
     */
    override fun delete(entity: T?): Boolean {
        return entity?.let { deleteById(identity(it))} ?: false
    }

    /**
     * deletes the entity in memory
     *
     * @param id
     */
    override fun deleteById(id: TId): Boolean {
        val count = update("delete from ${name()} where ${id()} = $id;")
        return count > 0
    }

    /**
     * deletes all entities from the datastore using the ids
     * @param ids
     * @return
     */
    override fun deleteByIds(ids: List<TId>): Int {
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
    override fun deleteByFields(conditions: List<Triple<String, Op, Any?>>): Int {
        val first = conditions.first()
        val query = query().where(first.first, first.second, first.third)
        if(conditions.size > 1) {
            conditions.tail().forEach { query.and(it.first, it.second, it.third) }
        }
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
    override fun getById(id: TId): T? {
        return sqlMapOne("select * from ${name()} where ${id()} = $id;")
    }

    /**
     * gets all the entities using the supplied ids
     * @param ids
     * @return
     */
    override fun getByIds(ids: List<TId>): List<T> {
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

    override fun seq(count: Int, desc: Boolean): List<T> {
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        val sql = "select * from " + name() + orderBy + " limit " + count
        val items = sqlMapMany(sql) ?: listOf<T>()
        return items
    }

    /**
     * Gets the total number of records based on the query provided.
     */
    override fun countByQuery(query: IQuery): Long {
        val filter = query.toFilter()
        val sql = "select count( * ) from ${name()} where " + filter
        val count = getScalarLong(sql)
        return count
    }

    override fun findByQuery(query: IQuery): List<T> {
        val filter = query.toFilter()
        val sql = "select * from ${name()} where " + filter
        val results = sqlMapMany(sql)
        return results ?: listOf()
    }

    /**
     * finds items based on the conditions
     */
    override fun findByFields(conditions: List<Triple<String, Op, Any>>): List<T> {
        val first = conditions.first()
        val tail = conditions.tail()
        val query = query().where(first.first, first.second, first.third)
        tail.forEach {
            query.and(it.first, Op.Eq, it.second)
        }
        return findByQuery(query)
    }

    /**
     * finds first item based on the query
     * @param query: name of field
     * @return
     */
    override fun findFirst(query: IQuery): T? {
        return findByQuery(query.limit(1)).firstOrNull()
    }

    /**
     * Updates records using sql provided and returns the number of updates made
     */
    protected open fun update(sql: String): Int {
        val count = db.update(sql)
        return count
    }

    protected open fun getScalarLong(sql: String): Long {
        return db.getScalarLong("select count(*) from ${name()};", null)
    }

    protected open fun sqlMapMany(sql: String): List<T>? {
        return db.mapAll(sql, null) { record -> mapper.decode(record, null) }
    }

    protected open fun sqlMapOne(sql: String): T? {
        return db.mapOne<T>(sql, null) { record ->  mapper.decode(record, null) }
    }

//    override fun findByProc(name: String, args: List<Any>?): List<T>? {
//        return db.callQueryMapped(name, {r -> mapper.decode(r, null) }, args)
//    }
//
//    override fun updateByProc(name: String, args: List<Any>?): Int {
//        return db.callUpdate(name, args)
//    }
}

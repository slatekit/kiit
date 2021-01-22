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
import slatekit.data.core.Meta
import slatekit.data.features.Countable
import slatekit.data.features.Orderable
import slatekit.entities.core.EntityInfo
import slatekit.data.statements.Statements
import slatekit.query.IQuery
import slatekit.query.Op

/**
 *
 * @param db : Db wrapper to execute sql
 * @param info : Holds all info relevant state/members needed to perform repo operations
 * @tparam T
 */
open class EntitySqlRepo<TId, T>(
    val db: IDb,
    override val info: EntityInfo,
    override val meta: Meta<TId, T>,
    val stmts: Statements<TId, T>,
    val mapper: EntityMapper<TId, T>
) : EntityRepo<TId, T>, Countable<TId, T>, Orderable<TId, T> where TId : Comparable<TId>, T : Any {

    /**
     * Gets the name of the repository/table
     */
    override val name: String = "${info.encodedChar}" + super.name + "${info.encodedChar}"

    /**
     * Creates the entity in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun create(entity: T): TId {
        val result = stmts.insert.prep(entity)
        val id = db.insertGetId(result.sql, result.values)
        return info.idInfo.convertToId(id, info.idType)
    }

    /**
     * Updates the entity in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun update(entity: T): Boolean {
        val result = stmts.update.prep(entity)
        val count = db.update(result.sql, result.values)
        return count > 0
    }

    /**
     * Gets the entity associated with the id from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun getById(id: TId): T? {
        val result = stmts.select.prep(id)
        return mapOne(result.sql, result.values)
    }

    /**
     * Gets the entities in the repository/table with matching ids
     * Note: You can customize the sql by providing your own statements
     */
    override fun getByIds(ids: List<TId>): List<T> {
        val result = stmts.select.prep(ids)
        val items = mapAll(result.sql, result.values)
        return items ?: listOf()
    }

    /**
     * Gets all entities in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun getAll(): List<T> {
        val sql = stmts.select.load()
        val items = mapAll(sql)
        return items ?: listOf<T>()
    }

    /**
     * Delete the entity from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun delete(entity: T?): Boolean {
        return entity?.let { deleteById(identity(it)) } ?: false
    }

    /**
     * Delete the entity from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun deleteById(id: TId): Boolean {
        val result = stmts.delete.prep(id)
        val count = update(result.sql, result.values)
        return count > 0
    }

    /**
     * Delete the entities from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun deleteByIds(ids: List<TId>): Int {
        val result = stmts.delete.prep(ids)
        val count = update(result.sql, result.values)
        return count
    }

    /**
     * Delete all entities from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun deleteAll(): Long {
        val sql = stmts.delete.drop()
        val count = update(sql)
        return count.toLong()
    }

    /** ====================================================================================
     * Custom methods beyond simple CRUD
     * Implementations for all the interfaces
     * @see[slatekit.data.features.Countable]
     * @see[slatekit.data.features.Deletable]
     * @see[slatekit.data.features.Countable]
     * ====================================================================================
     */

    /**
     * Gets total number of records in the repository/table
     */
    override fun count(): Long {
        val prefix = stmts.select.count()
        val sql = "$prefix;"
        val count = getScalarLong(sql)
        return count
    }

    override fun seq(count: Int, desc: Boolean): List<T> {
        val prefix = stmts.select.prefix()
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        val sql = "$prefix $orderBy limit $count;"
        val items = mapAll(sql) ?: listOf<T>()
        return items
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
     * Patch items using the fields/conditions
     */
    override fun patchByFields(fields: List<Pair<String, Any?>>, conditions: List<Triple<String, Op, Any?>>): Int {
        val prefix = stmts.update.prefix()
        val query = query()
        fields.forEach { query.set(it.first, it.second) }
        conditions.forEach { query.where(it.first, Op.Eq, it.second) }
        val updateSql = query.toUpdatesText()
        val sql = "$prefix $updateSql"
        return update(sql)
    }

    /**
     * Delete all entities from the repository/table matching the conditions
     * Note: You can customize the sql by providing your own statements
     * @param conditions: e.g. listOf(Triple("category", Op.Eq, "sci-fi" )
     * @return
     */
    override fun deleteByFields(conditions: List<Triple<String, Op, Any?>>): Int {
        val first = conditions.first()
        val query = query().where(first.first, first.second, first.third)
        if (conditions.size > 1) {
            conditions.tail().forEach { query.and(it.first, it.second, it.third) }
        }
        val filter = query.toFilter()
        val prefix = stmts.delete.prefix()
        val sql = "$prefix where $filter"
        return update(sql)
    }

    /** ====================================================================================
     * Methods using @see[slatekit.query.Query]
     * ====================================================================================
     */

    /**
     * updates items using the query
     * @param query: The query builder
     */
    override fun updateByQuery(query: IQuery): Int {
        val prefix = stmts.update.prefix()
        val updateSql = query.toUpdatesText()
        val sql = "$prefix $updateSql;"
        return update(sql)
    }

    /**
     * deletes items using the query
     * @param query: The query builder
     * @return
     */
    override fun deleteByQuery(query: IQuery): Int {
        val prefix = stmts.delete.prefix()
        val filter = query.toFilter()
        val sql = "$prefix where $filter;"
        return update(sql)
    }

    /**
     * Gets the total number of records based on the query provided.
     */
    override fun countByQuery(query: IQuery): Long {
        val prefix = stmts.select.count()
        val filter = query.toFilter()
        val sql = "$prefix where $filter;"
        val count = getScalarLong(sql)
        return count
    }

    /**
     * Finds items using the query builder
     */
    override fun findByQuery(query: IQuery): List<T> {
        val prefix = stmts.select.prefix()
        val filter = query.toFilter()
        val sql = "$prefix where $filter;"
        val results = mapAll(sql)
        return results ?: listOf()
    }

    /**
     * finds first item based on the query
     * @param query: name of field
     * @return
     */
    override fun findOneByQuery(query: IQuery): T? {
        return findByQuery(query.limit(1)).firstOrNull()
    }

    /**
     * Updates records using sql provided and returns the number of updates made
     */
    protected open fun update(sql: String, inputs:List<Any?>? = null): Int {
        val count = db.update(sql, inputs)
        return count
    }

    protected open fun getScalarLong(sql: String): Long {
        return db.getScalarLong(sql, null)
    }

    protected open fun mapAll(sql: String, inputs:List<Any?>? = null): List<T>? {
        return db.mapAll(sql, inputs) { record -> mapper.decode(record, null) }
    }

    protected open fun mapOne(sql: String, inputs:List<Any?>? = null): T? {
        return db.mapOne<T>(sql, inputs) { record -> mapper.decode(record, null) }
    }

//    override fun findByProc(name: String, args: List<Any>?): List<T>? {
//        return db.callQueryMapped(name, {r -> mapper.decode(r, null) }, args)
//    }
//
//    override fun updateByProc(name: String, args: List<Any>?): Int {
//        return db.callUpdate(name, args)
//    }
}

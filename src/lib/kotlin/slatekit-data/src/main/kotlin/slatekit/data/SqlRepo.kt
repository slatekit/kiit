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

package slatekit.data

import slatekit.common.data.*
import slatekit.data.core.Meta
import slatekit.data.syntax.Syntax
import slatekit.data.slatekit.data.features.Scriptable
import slatekit.query.IQuery

/**
 *
 * @param db : Db wrapper to execute sql
 * @param syntax: Used to build the sql syntax that may be vendor specific
 * @param mapper: Used to map a model T to/from the repo/table
 * @tparam T
 */
open class SqlRepo<TId, T>(
    val db: IDb,
    override val meta: Meta<TId, T>,
    val mapper: Mapper<TId, T>,
    val syntax: Syntax<TId, T>
) : FullRepo<TId, T>, Scriptable<TId, T> where TId : Comparable<TId>, T : Any {

    /**
     * Gets the name of the repository/table
     */
    override val name: String get() { return "${meta.table.encodeChar}" + super.name + "${meta.table.encodeChar}" }

    /**
     * Creates the entity in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun create(entity: T): TId {
        val result = syntax.insert.stmt(entity)
        val id = db.insertGetId(result)
        return meta.id.convertToId(id)
    }

    /**
     * Updates the entity in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun update(entity: T): Boolean {
        val result = syntax.update.stmt(entity)
        val count = db.update(result)
        return count > 0
    }

    /**
     * Gets the entity associated with the id from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun getById(id: TId): T? {
        val result = syntax.select.stmt(id)
        return mapOne(result)
    }

    /**
     * Gets the entities in the repository/table with matching ids
     * Note: You can customize the sql by providing your own statements
     */
    override fun getByIds(ids: List<TId>): List<T> {
        val result = syntax.select.stmt(ids)
        val items = mapAll(result)
        return items ?: listOf()
    }

    /**
     * Gets all entities in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun getAll(): List<T> {
        val sql = syntax.select.load()
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
        val result = syntax.delete.stmt(id)
        val count = update(result)
        return count > 0
    }

    /**
     * Delete the entities from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun deleteByIds(ids: List<TId>): Int {
        val result = syntax.delete.stmt(ids)
        val count = update(result)
        return count
    }

    /**
     * Delete all entities from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun deleteAll(): Long {
        val sql = syntax.delete.drop()
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
        val prefix = syntax.select.count()
        val sql = "$prefix;"
        val count = getScalarLong(sql)
        return count
    }

    override fun seq(count: Int, desc: Boolean): List<T> {
        val sql = syntax.select.take(count, desc)
        val items = mapAll(sql) ?: listOf<T>()
        return items
    }

    /**
     * updates items using the query
     * @param query: The query builder
     */
    override fun updateByQuery(query: IQuery): Int {
        val prefix = syntax.update.prefix()
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
        val prefix = syntax.delete.prefix()
        val filter = query.toFilter()
        val sql = "$prefix where $filter;"
        return update(sql)
    }

    /**
     * Gets the total number of records based on the query provided.
     */
    override fun countByQuery(query: IQuery): Long {
        val prefix = syntax.select.count()
        val filter = query.toFilter()
        val sql = "$prefix where $filter;"
        val count = getScalarLong(sql)
        return count
    }

    /**
     * Finds items using the query builder
     */
    override fun findByQuery(query: IQuery): List<T> {
        val prefix = syntax.select.prefix()
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

    override fun createByProc(name: String, args: List<Any>?): TId {
        val idText = db.callCreate(name, args)
        return meta.id.convertToId(idText)
    }

    override fun updateByProc(name: String, args: List<Any>?): Long {
        return db.callUpdate(name, args).toLong()
    }

    override fun findByProc(name: String, args: List<Any>?): List<T>? {
        return db.callQueryMapped(name, {r -> mapper.decode(r, null) }, args)
    }

    override fun deleteByProc(name: String, args: List<Any>?): Long {
        return db.callUpdate(name, args).toLong()
    }

    /**
     * finds items based on the conditions
     */
    override fun findByFilters(filters: List<Filter>, logical: Logical): List<T> {
        val command = syntax.select.filter(filters, logical)
        val items = mapAll(command.sql, command.values)
        return items ?: listOf()
    }

    /**
     * Patch items using the fields/conditions
     */
    override fun patchByFilters(fields: List<Value>, filters: List<Filter>, logical: Logical): Int {
        val command = syntax.update.patch(fields, filters, logical)
        return db.update(command.sql, command.values)
    }

    /**
     * Delete all entities from the repository/table matching the conditions
     * Note: You can customize the sql by providing your own statements
     * @param filters: e.g. listOf(Filter("category", Op.Eq, "sci-fi" )
     * @return
     */
    override fun deleteByFilters(filters: List<Filter>, logical: Logical): Int {
        val command = syntax.delete.filter(filters, logical)
        return db.update(command.sql, command.values)
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
}

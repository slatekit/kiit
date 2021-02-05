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
import slatekit.data.slatekit.data.Mapper
import slatekit.data.slatekit.data.features.Scriptable
import slatekit.data.sql.Dialect
import slatekit.data.sql.Provider
import slatekit.query.*

/**
 *
 * @param db : Database interface that can execute sql statements
 * @param meta: Provides ability to inspect the id, table for handling primary keys
 * @param provider: Vendor specific provider to handle sql dialect and builders
 * @param mapper: Used to map a model T to/from a database table
 * @param hooks : Optional hook to send out events on Create, Update, Delete
 * @tparam T
 */
open class SqlRepo<TId, T>(
    val db: IDb,
    meta: Meta<TId, T>,
    val mapper: Mapper<TId, T>,
    val provider: Provider<TId, T>,
    hooks: DataHooks<TId, T>? = null
) : BaseRepo<TId, T>(meta, hooks), FullRepo<TId, T>, Scriptable<TId, T> where TId : Comparable<TId>, T : Any {

    override val dialect: Dialect = provider.dialect

    /**
     * Creates the entity in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun create(entity: T): TId {
        val command = provider.insert.build(entity)
        val rawId = db.insertGetId(command.sql, command.pairs)
        val id = meta.id.convertToId(rawId)
        val success = isPersisted(id)
        notify(DataAction.Create, id, entity, success)
        return id
    }

    /**
     * Updates the entity in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun update(entity: T): Boolean {
        val id = identity(entity)
        val command = provider.update.build(entity)
        val count = db.update(command.sql, command.pairs)
        val success = count > 0
        notify(DataAction.Update, id, entity, success)
        return success
    }

    /**
     * updates items using the query
     * @param builder: The query builder
     */
    override fun patchByQuery(builder: Update): Int {
        val command = builder.build()
        return update(command.sql, command.pairs)
    }

    /**
     * Gets the entity associated with the id from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun getById(id: TId): T? {
        return findByQuery(select().where(meta.pkey.name, Op.Eq, id)).firstOrNull()
    }

    /**
     * Gets the entities in the repository/table with matching ids
     * Note: You can customize the sql by providing your own statements
     */
    override fun getByIds(ids: List<TId>): List<T> {
        return findByQuery(select().where(meta.pkey.name, Op.In, ids))
    }

    /**
     * Gets all entities in the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun getAll(): List<T> {
        return findByQuery(select())
    }

    /**
     * Finds items using the query builder
     */
    override fun findByQuery(builder: Select): List<T> {
        val command = builder.build()
        val results = mapAll(command.sql, command.pairs)
        return results ?: listOf()
    }

    /**
     * Delete the entity from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun delete(entity: T?): Boolean {
        return entity?.let { internalDeleteById(identity(it), entity) } ?: false
    }

    /**
     * Delete the entity from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun deleteById(id: TId): Boolean {
        return internalDeleteById(id)
    }

    /**
     * Delete the entities from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun deleteByIds(ids: List<TId>): Int {
        return deleteByQuery(delete().where(meta.pkey.name, Op.In, ids))
    }

    /**
     * Delete all entities from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    override fun deleteAll(): Long {
        return deleteByQuery(delete()).toLong()
    }

    /**
     * deletes items using the query
     * @param query: The query builder
     * @return
     */
    override fun deleteByQuery(builder: Delete): Int {
        val command = builder.build()
        val count = update(command.sql, command.pairs)
        return count
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
        return this.count() { }.toLong()
    }

    override fun seq(count: Int, order: Order): List<T> {
        return findByQuery(select().limit(count).orderBy(meta.pkey.name, order))
    }

    override fun scalar(sql: String, args: Values): Double {
        return db.getScalarDouble(sql, args)
    }

    override fun scalar(builder: Select.() -> Unit): Double {
        val s = select()
        builder(s)
        val command = s.build()
        return db.getScalarDouble(command.sql, command.pairs)
    }

    override fun createByProc(name: String, args: List<Value>?): TId {
        val idText = db.callCreate(name, args)
        return meta.id.convertToId(idText)
    }

    override fun updateByProc(name: String, args: List<Value>?): Long {
        return db.callUpdate(name, args).toLong()
    }

    override fun findByProc(name: String, args: List<Value>?): List<T>? {
        return db.callQueryMapped(name, { r -> mapper.decode(r, null) }, args)
    }

    override fun deleteByProc(name: String, args: List<Value>?): Long {
        return db.callUpdate(name, args).toLong()
    }

    protected open fun update(sql: String, inputs: List<Value>? = null): Int {
        val count = db.update(sql, inputs)
        return count
    }

    protected open fun mapAll(sql: String, inputs: List<Value>? = null): List<T>? {
        return db.mapAll(sql, inputs) { record -> mapper.decode(record, null) }
    }

    protected open fun mapOne(sql: String, inputs: List<Value>? = null): T? {
        return db.mapOne<T>(sql, inputs) { record -> mapper.decode(record, null) }
    }

    override fun delete(): Delete {
        return provider.delete(meta.table)
    }

    override fun select(): Select {
        return provider.select(meta.table)
    }

    override fun patch(): Update {
        return provider.patch(meta.table)
    }

    /**
     * Delete the entity from the repository/table
     * Note: You can customize the sql by providing your own statements
     */
    protected fun internalDeleteById(id: TId, entity: T? = null): Boolean {
        val command = delete().where(meta.pkey.name, Op.Eq, id).build()
        val count = update(command.sql, command.pairs)
        val success = count > 0
        notify(DataAction.Create, id, entity, success)
        return success
    }
}

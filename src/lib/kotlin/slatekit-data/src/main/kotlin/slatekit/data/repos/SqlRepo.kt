package slatekit.data.repos

import slatekit.common.data.DataAction
import slatekit.common.data.IDb
import slatekit.common.data.Mapper
import slatekit.common.ext.insertAt
import slatekit.data.core.*
import slatekit.data.core.SqlBased
import slatekit.data.core.EntityUtils

/**
 * SQL based Repository ( representing a database table )
 * @param db: @see[slatekit.common.data.IDb] to for low level database tasks (inserts/updates/deletes/queries)
 * @param info: Stores information about the Entity/Class assocated with this Repository/table
 */
open class SqlRepo<TId, T>(override val db:IDb,
                           override val mapper: Mapper<TId, T>,
                           override val info:EntityInfo)
    : CrudRepo<TId, T>, SqlBased<TId, T> where TId : Comparable<TId> {

    override fun create(entity: T): TId {
        val values = mapper.encode(entity, DataAction.Create, null)
        val cols = values.map { it.name }.joinToString(",")
        val vals = values.map { it.value }
        val sql = "insert into ${name()}($cols)"
        val idText = db.insertGetId(sql, vals)
        val id = EntityUtils.convertToId<TId>(idText, info.idType)
        return id
    }

    override fun update(entity: T): Boolean {
        val values = mapper.encode(entity, DataAction.Update, null)
        val updates = values.map { "${it.name} = ?"}.joinToString(",")
        val vals = values.map { it.value }
        val sql = "update ${name()} ($updates) where id = ?"
        val id = identity(entity)
        val updated = db.update(sql, vals.insertAt(0, id))
        return updated > 0
    }

    override fun getById(id: TId): T? {
        val sql = "select * from ${name()} where id = ?"
        val values = listOf(id)
        return sqlMapOne(sql, values)
    }

    override fun getByIds(ids: List<TId>): List<T> {
        val sql = "select * from ${name()} where id in (?)"
        val values = ids.joinToString(",")
        return sqlMapMany(sql, values) ?: listOf()
    }

    override fun getAll(): List<T> {
        val sql = "select * from ${name()}"
        return sqlMapMany(sql, null) ?: listOf()
    }

    override fun delete(entity: T?): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: TId): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteByIds(ids: List<TId>): Int {
        TODO("Not yet implemented")
    }

    override fun deleteAll(): Long {
        TODO("Not yet implemented")
    }

    protected open fun sqlScalarLong(sql: String, inputs:List<Any?>? = null): Long {
        return db.getScalarLong("select count(*) from ${name()};", inputs)
    }

    protected open fun sqlMapOne(sql: String, inputs:List<Any?>? = null): T? {
        return db.mapOne<T>(sql, inputs) { record ->  mapper.decode(record, info.encryptor) }
    }

    protected open fun sqlMapMany(sql: String, inputs:List<Any?>? = null): List<T>? {
        return db.mapAll<T>(sql, inputs) { record -> mapper.decode(record, info.encryptor) }
    }
}

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

package slatekit.orm.repos

import slatekit.db.Db
import slatekit.common.encrypt.Encryptor
import slatekit.common.ids.UUIDLCase
import slatekit.common.naming.Namer
import slatekit.common.toUUId
import slatekit.meta.KTypes
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query
import slatekit.orm.core.Entity
import slatekit.orm.core.EntityMapper
import slatekit.orm.core.EntityRepo
import kotlin.reflect.KClass

/**
 *
 * @param entityType : The data type of the entity/model
 * @param entityIdType : The data type of the primary key/identity field
 * @param entityMapper : The entity mapper that maps to/from entities / records
 * @param nameOfTable : The name of the table ( defaults to entity name )
 * @param _db
 * @tparam T
 */
abstract class EntityRepoSql<TId, T>(
        db: Db,
        entityType: KClass<*>,
        entityIdType: KClass<*>? = null,
        entityMapper: EntityMapper? = null,
        nameOfTable: String? = null,
        encryptor: Encryptor? = null,
        namer: Namer? = null,
        encodedChar: Char = '`',
        query: (() -> Query)? = null,
        val lastId: String? = null
) : EntityRepo<TId, T>(entityType, entityIdType, entityMapper, nameOfTable, encryptor, namer, encodedChar, query)
        where TId:Comparable<TId>, T:Entity<TId> {

    protected val _db = db


    override fun repoName(): String = "$encodedChar" + super.repoName() + "$encodedChar"


    override fun create(entity: T): TId {
        val mapper = _entityMapper
        val inserts = _entityMapper.converter.inserts
        val sql = inserts.sql(entity, mapper.model(), mapper)
        val id = _db.insertAndGetStringId(sql)
        return convertToId(id)
    }

    override fun update(entity: T): Boolean {
        val mapper = _entityMapper
        val updates = _entityMapper.converter.updates
        val sql = updates.sql(entity, mapper.model(), mapper)
        val count = sqlExecute(sql)
        return count > 0
    }

    /**
     * updates the table field using the value supplied
     * @param field: The field name
     * @param value: The value to set
     */
    override fun updateByField(field: String, value: Any): Int {
        val query = Query().set(field, value)
        val updateSql = query.toUpdatesText()
        val sql = "update " + repoName() + updateSql
        return _db.update(sql)
    }

    /**
     * updates items using the proc and args
     */
    override fun updateByProc(name: String, args: List<Any>?): Int {
        return _db.callUpdate(name, args)
    }

    /**
     * updates items using the query
     * @param query: The query builder
     */
    override fun updateByQuery(query: IQuery): Int {
        val updateSql = query.toUpdatesText()
        val sql = "update " + repoName() + updateSql
        return _db.update(sql)
    }

    /**
     * deletes the entity in memory
     *
     * @param id
     */
    override fun delete(id: TId): Boolean {
        val count = sqlExecute("delete from ${repoName()} where ${idName()} = $id;")
        return count > 0
    }

    /**
     * deletes all entities from the datastore using the ids
     * @param ids
     * @return
     */
    override fun delete(ids: List<TId>): Int {
        val delimited = ids.joinToString(",")
        val count = sqlExecute("delete from ${repoName()} where ${idName()} in ($delimited);")
        return count
    }

    /**
     * deletes items based on the field name and value
     * @param field: The field name
     * @param value: The value to check for
     * @return
     */
    override fun deleteByField(field: String, value: Any): Int {
        val query = Query().where(field, "=", value)
        val filter = query.toFilter()
        val sql = "delete from " + repoName() + " where " + filter
        return _db.update(sql)
    }

    /**
     * deletes items using the query
     * @param query: The query builder
     * @return
     */
    override fun deleteByQuery(query: IQuery): Int {
        val filter = query.toFilter()
        val sql = "delete from " + repoName() + " where " + filter
        return _db.update(sql)
    }

    /**
     * gets the entity associated with the id
     */
    override fun get(id: TId): T? {
        return sqlMapOne("select * from ${repoName()} where ${idName()} = $id;")
    }

    /**
     * gets all the entities using the supplied ids
     * @param ids
     * @return
     */
    override fun get(ids: List<TId>): List<T> {
        val delimited = ids.joinToString(",")
        return sqlMapMany("select * from ${repoName()} where ${idName()} in ($delimited);") ?: listOf()
    }

    override fun getAll(): List<T> {
        val result = sqlMapMany("select * from ${repoName()};")
        return result ?: listOf<T>()
    }

    override fun count(): Long {
        val count = _db.getScalarLong("select count(*) from ${repoName()};")
        return count
    }

    override fun top(count: Int, desc: Boolean): List<T> {
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        val sql = "select * from " + repoName() + orderBy + " limit " + count
        val items = _db.mapMany<T>(sql, _entityMapper) ?: listOf<T>()
        return items
    }


    /**
     * Gets the total number of records based on the query provided.
     */
    override fun count(query: IQuery):Long {
        val filter = query.toFilter()
        val sql = "select count( * ) from ${repoName()} where " + filter
        val count = _db.getScalarLong(sql)
        return count
    }

    override fun find(query: IQuery): List<T> {
        val filter = query.toFilter()
        val sql = "select * from ${repoName()} where " + filter
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
     * finds items by using the sql
     * @param query
     * @return
     */
    override fun findByProc(name: String, args: List<Any>?): List<T>? {
        return _db.callQueryMapped(name, _entityMapper, args)
    }

    protected open fun scriptLastId(): String = lastId ?: ""

    protected fun convertToId(id:String):TId {
        return when(this._entityType) {
            KTypes.KIntClass    -> id.toInt() as TId
            KTypes.KLongClass   -> id.toLong() as TId
            KTypes.KUUIDClass   -> id.toUUId() as TId
            else                -> id as TId
        }
    }

    private fun sqlExecute(sql: String): Int {
        return _db.update(sql)
    }

    private fun sqlMapMany(sql: String): List<T>? {
        return _db.mapMany<T>(sql, _entityMapper)
    }

    private fun sqlMapOne(sql: String): T? {
        return _db.mapOne<T>(sql, _entityMapper)
    }
}

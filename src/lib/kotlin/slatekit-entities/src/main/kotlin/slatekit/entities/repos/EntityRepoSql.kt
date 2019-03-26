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
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query
import slatekit.entities.Entity
import slatekit.entities.EntityMapper
import slatekit.entities.EntityRepo
import slatekit.meta.models.Model
import kotlin.reflect.KClass

/**
 *
 * @param entityType : The data type of the entity/model
 * @param entityIdType : The data type of the primary key/identity field
 * @param entityMapper : The entity mapper that maps to/from entities / records
 * @param nameOfTable : The name of the table ( defaults to entity name )
 * @param db
 * @tparam T
 */
abstract class EntityRepoSql<TId, T>(
        val db: IDb,
        entityType: KClass<*>,
        entityIdType: KClass<*>,
        val entityMapper: EntityMapper<TId, T>,
        nameOfTable: String,
        model: Model? = null,
        encryptor: Encryptor? = null,
        namer: Namer? = null,
        encodedChar: Char = '`',
        query: (() -> Query)? = null
) : EntityRepo<TId, T>(entityType, entityIdType, nameOfTable, encodedChar, model, encryptor, namer, query)
        where TId:Comparable<TId>, T: Entity<TId> {


    override fun repoName(): String = "$encodedChar" + super.repoName() + "$encodedChar"


    /**
     * updates the table field using the value supplied
     * @param field: The field name
     * @param value: The value to set
     */
    override fun updateByField(field: String, value: Any): Int {
        val query = Query().set(field, value)
        val updateSql = query.toUpdatesText()
        val sql = "update " + repoName() + updateSql
        return db.update(sql)
    }

    /**
     * updates items using the proc and args
     */
    override fun updateByProc(name: String, args: List<Any>?): Int {
        return db.callUpdate(name, args)
    }

    /**
     * updates items using the query
     * @param query: The query builder
     */
    override fun updateByQuery(query: IQuery): Int {
        val updateSql = query.toUpdatesText()
        val sql = "update " + repoName() + updateSql
        return db.update(sql)
    }

    /**
     * deletes the entity in memory
     *
     * @param id
     */
    override fun delete(id: TId): Boolean {
        val count = db.update("delete from ${repoName()} where ${idName()} = $id;")
        return count > 0
    }

    /**
     * deletes all entities from the datastore using the ids
     * @param ids
     * @return
     */
    override fun delete(ids: List<TId>): Int {
        val delimited = ids.joinToString(",")
        val count = db.update("delete from ${repoName()} where ${idName()} in ($delimited);")
        return count
    }

    /**
     * deletes all entities from the data store using the ids
     * @param ids
     * @return
     */
    override fun deleteAll(): Long {
        val count = db.update("delete from ${repoName()};")
        return count.toLong()
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
        return db.update(sql)
    }

    /**
     * deletes items using the query
     * @param query: The query builder
     * @return
     */
    override fun deleteByQuery(query: IQuery): Int {
        val filter = query.toFilter()
        val sql = "delete from " + repoName() + " where " + filter
        return db.update(sql)
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
        val count = db.getScalarLong("select count(*) from ${repoName()};", null)
        return count
    }

    override fun top(count: Int, desc: Boolean): List<T> {
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        val sql = "select * from " + repoName() + orderBy + " limit " + count
        val items = db.mapMany<T>(sql, entityMapper) ?: listOf<T>()
        return items
    }


    /**
     * Gets the total number of records based on the query provided.
     */
    override fun count(query: IQuery):Long {
        val filter = query.toFilter()
        val sql = "select count( * ) from ${repoName()} where " + filter
        val count = db.getScalarLong(sql, null)
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
        return db.callQueryMapped(name, entityMapper, args)
    }

    private fun sqlMapMany(sql: String): List<T>? {
        return db.mapMany(sql, entityMapper)
    }

    private fun sqlMapOne(sql: String): T? {
        return db.mapOne<T>(sql, entityMapper)
    }
}

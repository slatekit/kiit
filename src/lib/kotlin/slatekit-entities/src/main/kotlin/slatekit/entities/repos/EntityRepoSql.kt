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

import slatekit.common.db.Db
import slatekit.common.encrypt.Encryptor
import slatekit.common.query.IQuery
import slatekit.common.query.Query
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityMapper
import slatekit.entities.core.EntityRepo
import kotlin.reflect.KClass

/**
 *
 * @param entityType   : The data type of the entity/model
 * @param entityIdType : The data type of the primary key/identity field
 * @param entityMapper : The entity mapper that maps to/from entities / records
 * @param nameOfTable  : The name of the table ( defaults to entity name )
 * @param _db
 * @tparam T
 */
abstract class EntityRepoSql<T>
(
        db: Db,
        entityType: KClass<*>,
        entityIdType: KClass<*>? = null,
        entityMapper: EntityMapper? = null,
        nameOfTable: String? = null,
        encryptor: Encryptor? = null
)
    : EntityRepo<T>(entityType, entityIdType, entityMapper, nameOfTable, encryptor) where T : Entity {

    protected val _db = db

    override fun create(entity: T): Long {
        val sql = mapFields(entity, false)
        val id = _db.insert("insert into ${tableName()} " + sql + ";")
        return id
    }


    override fun update(entity: T): T {
        val sql = mapFields(entity, true)
        val id = entity.identity()
        sqlExecute("update ${tableName()} set " + sql + " where ${idName()} = $id;")
        return entity
    }


    /**
     * deletes the entity in memory
     *
     * @param id
     */
    override fun delete(id: Long): Boolean {
        val count = sqlExecute("delete from ${tableName()} where ${idName()} = ${id};")
        return count > 0
    }


    override fun get(id: Long): T? {
        return sqlMapOne("select * from ${tableName()} where ${idName()} = $id;")
    }


    override fun getAll(): List<T> {
        val result = sqlMapMany("select * from ${tableName()};")
        return result ?: listOf<T>()
    }


    override fun count(): Long {
        val count = _db.getScalarLong("select count(*) from ${tableName()};")
        return count
    }


    override fun find(query: IQuery): List<T> {
        val filter = query.toFilter()
        val sql = "select * from ${tableName()} where " + filter
        val results = sqlMapMany(sql)
        return results ?: listOf()
    }


    /**
     * finds items based on the query
     * @param query
     * @return
     */
    override fun findBy(field: String, op: String, value: Any): List<T> {
        return find(Query().where(field, op, value))
    }


    protected open fun scriptLastId(): String = ""


    private fun sqlExecute(sql: String): Int {
        return _db.update(sql)
    }


    private fun sqlMapMany(sql: String): List<T>? {
        return _db.mapMany<T>(sql, _entityMapper)
    }


    private fun sqlMapOne(sql: String): T? {
        return _db.mapOne<T>(sql, _entityMapper)
    }


    private fun mapFields(item: Entity, isUpdate: Boolean): String {
        return _entityMapper.mapToSql(item, isUpdate, false)
    }
}

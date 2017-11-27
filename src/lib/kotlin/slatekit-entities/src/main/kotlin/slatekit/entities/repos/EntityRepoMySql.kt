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
import slatekit.common.db.DbUtils
import slatekit.common.encrypt.Encryptor
import slatekit.common.query.IQuery
import slatekit.common.query.Query
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityMapper
import kotlin.reflect.KClass


/**
 * Repository class specifically for MySql
 * @param entityType   : The data type of the entity/model
 * @param entityIdType : The data type of the primary key/identity field
 * @param entityMapper : The entity mapper that maps to/from entities / records
 * @param nameOfTable  : The name of the table ( defaults to entity name )
 * @param db
 * @tparam T
 */
open class EntityRepoMySql<T>(
        db: Db,
        entityType: KClass<*>,
        entityIdType: KClass<*>? = null,
        entityMapper: EntityMapper? = null,
        nameOfTable: String? = null,
        encryptor: Encryptor? = null
)
    : EntityRepoSql<T>(db, entityType, entityIdType, entityMapper, nameOfTable, encryptor) where T : Entity {

    override fun top(count: Int, desc: Boolean): List<T> {
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        val sql = "select * from " + tableName() + orderBy + " limit " + count
        val items = _db.mapMany<T>(sql, _entityMapper) ?: listOf<T>()
        return items
    }


    /**
     * updates the table field using the value supplied
     * @param field: The field name
     * @param value: The value to set
     */
    override fun updateByField(field:String, value: Any): Int {
        val query = Query().set(field, value)
        val updateSql = query.toUpdatesText()
        val sql = "update " + tableName() + updateSql
        return _db.update(sql)
    }


    /**
     * updates items using the proc and args
     */
    override fun updateByProc(name:String, args:List<Any>?): Int {
        return _db.callUpdate(name, args)
    }


    /**
     * updates items using the query
     * @param query: The query builder
     */
    override fun updateByQuery(query: IQuery): Int {
        val updateSql = query.toUpdatesText()
        val sql = "update " + tableName() + updateSql
        return _db.update(sql)
    }


    /**
     * deletes items based on the field name and value
     * @param field: The field name
     * @param value: The value to check for
     * @return
     */
    override fun deleteByField(field:String, value: Any): Int {
        val query = Query().where(field, "=", value)
        val filter = query.toFilter()
        val sql = "delete from " + tableName() + " where " + filter
        return _db.update(sql)
    }


    /**
     * deletes items using the query
     * @param query: The query builder
     * @return
     */
    override fun deleteByQuery(query: IQuery): Int {
        val filter = query.toFilter()
        val sql = "delete from " + tableName() + " " + filter
        return _db.update(sql)
    }


    /**
     * finds items by using the sql
     * @param query
     * @return
     */
    override fun findByProc(name:String, args:List<Any>?): List<T>? {
        return _db.callQueryMapped(name, _entityMapper, args)
    }


    override fun scriptLastId(): String =
            "SELECT LAST_INSERT_ID();"


    override fun tableName(): String =
            "`" + super.tableName() + "`"
}

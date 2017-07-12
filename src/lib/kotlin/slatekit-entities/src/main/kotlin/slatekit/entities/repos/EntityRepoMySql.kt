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
        nameOfTable: String? = null
)
    : EntityRepoSql<T>(db, entityType, entityIdType, entityMapper, nameOfTable) where T : Entity {

    override fun top(count: Int, desc: Boolean): List<T> {
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        val sql = "select * from " + tableName() + orderBy + " limit " + count
        val items = _db.mapMany<T>(sql, _entityMapper) ?: listOf<T>()
        return items
    }


    override protected fun scriptLastId(): String =
            "SELECT LAST_INSERT_ID();"


    override fun tableName(): String =
            "`" + super.tableName() + "`"
}

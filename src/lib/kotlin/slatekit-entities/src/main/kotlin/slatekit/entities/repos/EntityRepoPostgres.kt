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
import kotlin.reflect.KClass

/**
 * Repository class specifically for MySql
 * @param entityType : The data type of the entity/model
 * @param entityIdType : The data type of the primary key/identity field
 * @param entityMapper : The entity mapper that maps to/from entities / records
 * @param nameOfTable : The name of the table ( defaults to entity name )
 * @param db
 * @tparam T
 */
open class EntityRepoPostgres<T>(
    db: Db,
    entityType: KClass<*>,
    entityIdType: KClass<*>? = null,
    entityMapper: EntityMapper? = null,
    nameOfTable: String? = null,
    encryptor: Encryptor? = null
)
    : EntityRepoMySql<T>(db, entityType, entityIdType, entityMapper, nameOfTable, encryptor) where T : Entity {


    override fun repoName(): String =
            "\"" + super.repoName() + "\""
}

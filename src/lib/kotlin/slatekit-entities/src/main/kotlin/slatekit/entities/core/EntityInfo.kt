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

package slatekit.entities.core

import slatekit.common.db.DbType
import slatekit.common.db.DbType.DbTypeMySql
import kotlin.reflect.KClass

/**
 *
 * @param entityType : the type of the entity
 * @param entityServiceType : the type of the service    ( EntityService[T] or derivative )
 * @param entityRepoType : the type of the repository ( EntityRepository[T] or derivative )
 * @param entityServiceInstance : an instance of the service ( singleton usage )
 * @param entityRepoInstance : an instance of the repo    ( singleton usage )
 * @param entityMapperInstance : an instance of the mapper  ( singleton usage )
 * @param dbType : the database provider type
 * @param dbKey : a key identifying the database connection
 *                               ( see DbLookup / Example_Database.scala )
 * @param dbShard : a key identifying the database shard
 *                               ( see DbLookup / Example_Database.scala )
 */
open class EntityInfo(
        val entityType: KClass<*>,
        val entityServiceType: KClass<*>,
        val entityRepoType: KClass<*>,
        val entityRepoInstance: IEntityRepo,
        val entityMapperInstance: EntityMapper<*,*>,
        val dbType: DbType = DbTypeMySql,
        val dbKey: String = "",
        val dbShard: String = "",
        val entityServiceInstance: IEntityService? = null
) {
    val entityTypeName = entityType.qualifiedName ?: ""
}

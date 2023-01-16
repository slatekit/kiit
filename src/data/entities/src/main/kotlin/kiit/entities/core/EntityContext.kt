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

package kiit.entities.core

import kotlin.reflect.KClass
import kiit.common.data.Vendor
import kiit.common.data.Vendor.MySql
import kiit.entities.EntityRepo
import kiit.entities.EntityService
import kiit.entities.mapper.EntityMapper
import kiit.meta.models.Model

/**
 *
 * @param entityType : the type of the entity
 * @param entityServiceInstance : an instance of the service ( singleton usage )
 * @param entityRepoInstance : an instance of the repo    ( singleton usage )
 * @param entityMapperInstance : an instance of the mapper  ( singleton usage )
 * @param vendor : the database provider type
 * @param dbKey : a key identifying the database connection
 *                               ( see Connections / Example_Database.kt )
 * @param dbShard : a key identifying the database shard
 *                               ( see Connections / Example_Database.kt )
 */
open class EntityContext(
    val entityType: KClass<*>,
    val entityIdType: KClass<*>,
    val entityServiceInstance: EntityService<*, *>,
    val entityRepoInstance: EntityRepo<*, *>,
    val entityMapperInstance: EntityMapper<*, *>,
    val vendor: Vendor = MySql,
    val model: Model,
    val dbKey: String = "",
    val dbShard: String = ""
) {
    val entityTypeName = entityType.qualifiedName ?: ""
    val entityRepoType: KClass<*> = entityRepoInstance::class
}

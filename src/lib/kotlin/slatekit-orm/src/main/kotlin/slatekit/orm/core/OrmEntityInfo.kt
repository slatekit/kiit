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

package slatekit.orm.core

import slatekit.common.newline
import slatekit.common.db.DbType
import slatekit.entities.core.EntityInfo
import slatekit.entities.core.EntityMapper
import slatekit.entities.core.IEntityRepo
import slatekit.entities.core.IEntityService
import slatekit.orm.databases.SqlBuilder
import slatekit.meta.kClass
import slatekit.meta.models.Model
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
class OrmEntityInfo(
        val model: Model,
        val entityDDL: SqlBuilder,
        entityType: KClass<*>,
        entityServiceType: KClass<*>,
        entityRepoType: KClass<*>,
        entityServiceInstance: IEntityService?,
        entityRepoInstance: IEntityRepo,
        entityMapperInstance: EntityMapper<*, *>,
        dbType: DbType,
        dbKey: String = "",
        dbShard: String = ""
) : EntityInfo(entityType, entityServiceType, entityRepoType, entityRepoInstance, entityMapperInstance, dbType, dbKey, dbShard, entityServiceInstance) {

    fun toStringDetail(): String {
        val text = "entity type  : " + entityTypeName + newline +
                "model        : " + model.fullName
                "svc     type : " + getTypeName(entityServiceType) + newline +
                "svc     inst : " + getTypeNameFromInst(entityServiceInstance) + newline +
                "repo    type : " + getTypeName(entityRepoType) + newline +
                "repo    inst : " + getTypeNameFromInst(entityRepoInstance) + newline +
                "mapper  type : " + getTypeName(entityMapperInstance::class) + newline +
                "mapper  inst : " + getTypeNameFromInst(entityMapperInstance) + newline +
                "db type      : " + dbType + newline +
                "db key       : " + dbKey + newline +
                "db shard     : " + dbShard + newline
        return text
    }

    private fun getTypeName(tpe: KClass<*>?): String = tpe?.qualifiedName ?: ""

    private fun getTypeNameFromInst(tpe: Any?): String = tpe?.kClass?.qualifiedName ?: ""
}

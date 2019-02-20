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

import slatekit.common.newline
import slatekit.db.DbType
import slatekit.db.DbType.DbTypeMySql
import slatekit.entities.databases.SqlBuilder
import slatekit.meta.kClass
import slatekit.meta.models.Model
import kotlin.reflect.KClass

/**
 *
 * @param entityType : the type of the entity
 * @param entityServiceType : the type of the service    ( EntityService[T] or derivative )
 * @param entityRepoType : the type of the repository ( EntityRepository[T] or derivative )
 * @param entityMapperType : the type of the mapper     ( EntityMapper[T] or derivative )
 * @param entityServiceInstance : an instance of the service ( singleton usage )
 * @param entityRepoInstance : an instance of the repo    ( singleton usage )
 * @param entityMapperInstance : an instance of the mapper  ( singleton usage )
 * @param dbType : the database provider type
 * @param dbKey : a key identifying the database connection
 *                               ( see DbLookup / Example_Database.scala )
 * @param dbShard : a key identifying the database shard
 *                               ( see DbLookup / Example_Database.scala )
 */
data class EntityInfo(
        val entityType: KClass<*>,
        val model: Model,
        val entityServiceType: KClass<*>? = null,
        val entityRepoType: KClass<*>? = null,
        val entityMapperType: KClass<*>? = null,
        val entityServiceInstance: IEntityService? = null,
        val entityRepoInstance: IEntityRepo? = null,
        val entityMapperInstance: EntityMapper? = null,
        val entityDDL: SqlBuilder? = null,
        val dbType: DbType = DbTypeMySql,
        val dbKey: String = "",
        val dbShard: String = ""
) {
    val entityTypeName = entityType.qualifiedName ?: ""

    fun toStringDetail(): String {
        val text = "entity type  : " + entityTypeName + newline +
                "model        : " + model.fullName
                "svc     type : " + getTypeName(entityServiceType) + newline +
                "svc     inst : " + getTypeNameFromInst(entityServiceInstance) + newline +
                "repo    type : " + getTypeName(entityRepoType) + newline +
                "repo    inst : " + getTypeNameFromInst(entityRepoInstance) + newline +
                "mapper  type : " + getTypeName(entityMapperType) + newline +
                "mapper  inst : " + getTypeNameFromInst(entityMapperInstance) + newline +
                "db type      : " + dbType + newline +
                "db key       : " + dbKey + newline +
                "db shard     : " + dbShard + newline
        return text
    }

    fun getTypeName(tpe: KClass<*>?): String = tpe?.qualifiedName ?: ""

    fun getTypeNameFromInst(tpe: Any?): String = tpe?.kClass?.qualifiedName ?: ""
}

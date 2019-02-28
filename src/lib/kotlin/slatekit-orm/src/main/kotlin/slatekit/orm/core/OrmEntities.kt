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

import slatekit.common.naming.Namer
import slatekit.common.db.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.db.DbType
import slatekit.db.Db
import slatekit.entities.core.*
import slatekit.meta.models.Model
import kotlin.reflect.KClass

/**
 *  A registry for all the entities and their corresponding services, repositories, database
 *  types, and connection keys.
 *
 *   // Case 1: In-memory
 *   Entities.Register[Invitation](sqlRepo: false)
 *
 *   // Case 2: In-memory, with custom service
 *   Entities.register[Invitation](sqlRepo: false, serviceType: typeof(InvitationService));
 *
 *   // Case 3: Sql-repo
 *   Entities.register[Invitation](sqlRepo: true)
 *
 *   // Case 4: Sql-repo, with custom service
 *   Entities.register[Invitation](sqlRepo: true, serviceType: typeof(InvitationService));
 *
 *   // Case 5: Custom repository
 *   Entities.register[Invitation](sqlRepo: true, repo: InvitationRepository());
 *
 *   // Case 6: Custom repo with provider type specified
 *   Entities.register[Invitation](sqlRepo: true, repo: InvitationRepository(),
 *     dbType: "mysql");
 *
 *   // Case 7: Full customization
 *   Entities.register[Invitation](sqlRepo: true, serviceType: typeof(InvitationService),
 *     repo: InvitationRepository(), dbType: "mysql");
 *
 *   // Case 8: Full customization
 *   Entities.register[Invitation](sqlRepo: true, serviceType: typeof(InvitationService),
 *     repo: InvitationRepository(), mapper: null, dbType: "mysql");
 *
 */
class OrmEntities(
        dbs: DbLookup? = DbLookup.defaultDb(DbCon.empty),
        enc: Encryptor? = null,
        logs: Logs = LogsDefault,
        namer: Namer? = null
) : Entities<OrmEntityInfo>({ con -> Db(con) }, dbs, enc, logs, namer) {

    val builder2:OrmBuilder = OrmBuilder(dbs, enc)

    fun <TId, T> register(
            entityType: KClass<*>,
            model: Model? = null,
            tableName: String,
            serviceType: KClass<*>? = null,
            repository: EntityRepo<TId, T>? = null,
            mapper: EntityMapper<TId,T>? = null,
            dbType: DbType = DbType.DbTypeMemory,
            dbKey: String? = null,
            dbShard: String? = null,
            serviceCtx: Any? = null,
            persistUTC: Boolean = false
    ): OrmEntityInfo where TId:Comparable<TId>, T: Entity<TId> {

        // 1. Model ( schema of the entity )
        val entityModel = model ?: builder2.model(entityType, namer, tableName)

        // 2. Mapper ( maps entities to/from sql using the model/schema )
        val entityMapper = mapper ?: builder2.mapper(dbType, entityModel, persistUTC, enc, namer)

        // 3. Repo ( provides CRUD using the Mapper)
        val entityRepo = repository ?: builder2.repo(dbType, dbKey ?: "", dbShard ?: "", entityType, entityMapper, tableName)
        val repoType = entityRepo::class

        // 4. Service ( used to provide validation, placeholder for business functionality )
        val entityService = builder2.service(this, serviceType, entityRepo, serviceCtx)
        val svcType = entityService::class

        // 5. DDL ( for table creation schema, ddl management )
        val entityDdl = builder2.ddl(dbType, namer)

        // 6. Now store all the info for easy lookup
        val info = OrmEntityInfo(
                entityModel,
                entityDdl,
                entityType,
                svcType,
                repoType,
                entityService,
                entityRepo,
                entityMapper,
                dbType,
                dbKey ?: "",
                dbShard ?: ""
        )
        val key = builder.key(entityType, dbKey ?: "", dbShard ?: "")
        _info = _info.add(key, info)
        _mappers.put(entityType.qualifiedName!!, entityMapper)
        return info
    }


    /**
     * Gets a registered model ( schema for an entity ) for the entity type
     */
    fun getModel(entityType: KClass<*>): Model {
        val entityKey = builder.key(entityType)
        if (!_info.contains(entityKey)) {
            logger.error("Model not found for $entityKey")
            throw IllegalArgumentException("model not found for: " + entityType.qualifiedName)
        }
        return _info.get(entityKey)?.model!!
    }
}

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

import slatekit.common.utils.ListMap
import slatekit.common.naming.Namer
import slatekit.common.db.*
import slatekit.common.db.DbType.DbTypeMySql
import slatekit.common.db.DbType.DbTypePGres
import slatekit.common.db.types.DbSource
import slatekit.common.db.types.DbSourceMySql
import slatekit.common.db.types.DbSourcePostGres
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.entities.support.EntityComponentBuilder
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
class Entities(
        private val _dbs: DbLookup? = DbLookup.defaultDb(DbConEmpty),
        val enc: Encryptor? = null,
        val logs: Logs = LogsDefault,
        val namer: Namer? = null
) {

    private var _info = ListMap<String, EntityInfo>(listOf())
    private val _mappers = mutableMapOf<String, EntityMapper>()
    private val logger = logs.getLogger("db")
    val builder = EntityComponentBuilder(_dbs, enc)

    fun <T> register(
            entityType: KClass<*>,
            model: Model? = null,
            serviceType: KClass<*>? = null,
            repoType: KClass<*>? = null,
            mapperType: KClass<*>? = null,
            repository: EntityRepo<T>? = null,
            mapper: EntityMapper? = null,
            dbType: DbType = DbType.DbTypeMemory,
            dbKey: String? = null,
            dbShard: String? = null,
            tableName: String? = null,
            serviceCtx: Any? = null,
            persistUTC: Boolean = false
    ): EntityInfo where T : Entity {

        // 1. Model ( schema of the entity )
        val entityModel = model ?: builder.model(entityType, namer)

        // 2. Mapper ( maps entities to/from sql using the model/schema )
        val entityMapper = builder.mapper(dbType, entityModel, tableName, persistUTC, enc, namer)

        // 3. Repo ( provides CRUD using the Mapper)
        val entityRepo = builder.repo<T>(dbType, dbKey ?: "", dbShard ?: "", entityType, entityMapper, tableName)

        // 4. Service ( used to provide validation, placeholder for business functionality )
        val entityService = builder.service<T>(this, serviceType, entityRepo, serviceCtx)

        // 5. DDL ( for table creation schema, ddl management )
        val ddl = builder.ddl(dbType, namer)

        // 6. Now store all the info for easy lookup
        val info = EntityInfo(
                entityType,
                entityModel,
                serviceType,
                repoType,
                mapperType,
                entityService,
                entityRepo,
                entityMapper,
                ddl,
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
     * Gets the default database
     */
    fun getDb(): Db = builder.db()


    /**
     * Gets a database by its name/alias
     */
    fun getDbByName(name: String): Db = builder.db(name)


    /**
     * Gets a list of all the registered entities
     */
    fun getEntities(): List<EntityInfo> = _info.all()


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


    /**
     * Gets a registered mapper for the entity type
     */
    fun getMapper(entityType: KClass<*>): EntityMapper {
        val entityKey = entityType.qualifiedName
        if (!_mappers.contains(entityKey)) {
            logger.error("Mapper not found for $entityKey")
            throw IllegalArgumentException("mapper not found for :$entityKey")
        }

        val mapper = _mappers[entityKey]
        return mapper!!
    }

    /**
     * Get a registered repository for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getRepo(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityRepo<T> where T : Entity =
            getRepoByType(tpe, dbKey, dbShard) as EntityRepo<T>

    /**
     * Get a registered service for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getSvc(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityService<T> where T : Entity =
            getSvcByType(tpe, dbKey, dbShard) as EntityService<T>


    fun getInfo(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityInfo {
        val key = builder.key(entityType, dbKey, dbShard)
        require(_info.contains(key), { "Entity invalid or not registered with key : " + key })
        return _info.get(key)!!
    }

    fun getInfoByName(entityType: String, dbKey: String = "", dbShard: String = ""): EntityInfo {
        val key = builder.key(entityType, dbKey, dbShard)
        if (!_info.contains(key)) {
            logger.error("Mapper not found for $key")
            throw IllegalArgumentException("invalid entity : $key")
        }
        return _info.get(key)!!
    }


    fun getSvcByType(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): IEntityService {
        val info = getInfo(entityType, dbKey, dbShard)
        return info.entityServiceInstance!!
    }

    fun getSvcByTypeName(entityType: String, dbKey: String = "", dbShard: String = ""): IEntityService {
        val info = getInfoByName(entityType, dbKey, dbShard)
        return info.entityServiceInstance!!
    }

    /**
     * get the repository by class
     */
    fun getRepoByType(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): IEntityRepo {
        val info = getInfo(tpe, dbKey, dbShard)
        return info.entityRepoInstance!!
    }


    fun getDbSource(dbKey: String = "", dbShard: String = ""): DbSource {
        val dbType = builder.con(dbKey, dbShard)

        // Only supporting MySql for now.
        val source = dbType?.let { type ->
            when (type.driver) {
                DbTypeMySql.driver -> DbSourceMySql()
                DbTypePGres.driver -> DbSourcePostGres()
                else -> DbSourceMySql()
            }
        } ?: DbSourceMySql()
        return source
    }
}

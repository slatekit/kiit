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
import slatekit.common.db.DbType.DbTypeMemory
import slatekit.common.db.types.DbSource
import slatekit.common.db.types.DbSourceMySql
import slatekit.common.db.types.DbSourcePostGres
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.entities.databases.mysql.MySqlEntityDDL
import slatekit.meta.Reflector
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.entities.repos.EntityRepoMySql
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper
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
    private val _dbs: DbLookup? = null,
    val _enc: Encryptor? = null,
    val logs: Logs = LogsDefault,
    val namer: Namer? = null
) {

    private var _info = ListMap<String, EntityInfo>(listOf())
    private val _mappers = mutableMapOf<String, EntityMapper>()
    private val logger = logs.getLogger("db")

    fun <T> register(
        isSqlRepo: Boolean,
        entityType: KClass<*>,
        model: Model? = null,
        serviceType: KClass<*>? = null,
        repoType: KClass<*>? = null,
        mapperType: KClass<*>? = null,
        repository: EntityRepo<T>? = null,
        mapper: EntityMapper? = null,
        dbType: DbType? = DbTypeMySql,
        dbKey: String? = null,
        dbShard: String? = null,
        tableName: String? = null,
        serviceCtx: Any? = null
    ): EntityInfo where T : Entity {
        val db = if (!isSqlRepo) DbTypeMemory else dbType ?: DbTypeMySql

        // Create mapper
        val mapr = buildMapper(isSqlRepo, entityType, model, mapper, tableName)

        // Create repo
        val repo = buildRepo<T>(isSqlRepo, db, dbKey ?: "", dbShard ?: "", entityType, mapr, tableName)

        // Create the DDL ( for table creation schema, ddl management )
        val ddl = buildDDL(isSqlRepo, db)

        // Create the service
        val service = buildService<T>(serviceType, repo, serviceCtx)
        val entityModel = model ?: mapr.model()

        // Now register
        val info = EntityInfo(
                entityType,
                entityModel,
                serviceType,
                repoType,
                mapperType,
                service,
                repo,
                mapr,
                ddl,
                isSqlRepo,
                db,
                dbKey ?: "",
                dbShard ?: ""
        )
        val key = getKey(entityType, dbKey ?: "", dbShard ?: "")
        _info = _info.add(key, info)
        return info
    }

    fun getDbSource(dbKey: String = "", dbShard: String = ""): DbSource {
        val dbType = getDbCon(dbKey, dbShard)

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

    /**
     * get the repository by class
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getRepo(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityRepo<T> where T : Entity =
            getRepoByType(tpe, dbKey, dbShard) as EntityRepo<T>

    /**
     * get the repository by class
     */
    fun getRepoByType(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): IEntityRepo {
        val info = getInfo(entityType, dbKey, dbShard)
        return info.entityRepoInstance!!
    }

    /**
     * get service by class
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getSvc(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityService<T> where T : Entity =
            getSvcByType(tpe, dbKey, dbShard) as EntityService<T>

    fun getSvcByType(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): IEntityService {
        val info = getInfo(entityType, dbKey, dbShard)
        return info.entityServiceInstance!!
    }

    /**
     * Builds a new entity service instance.
     */
    fun buildSvcByType(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): IEntityService {
        val info = getInfo(entityType, dbKey, dbShard)
        val repo = getRepoByType(entityType, dbKey, dbShard) as EntityRepo<*>
        val svc = buildService(info.entityServiceType, repo, null)
        return svc
    }

    fun getServiceByName(entityType: String, dbKey: String = "", dbShard: String = ""): IEntityService {
        val info = getInfoByName(entityType, dbKey, dbShard)
        return info.entityServiceInstance!!
    }

    fun getMapper(entityType: KClass<*>): EntityMapper {
        val entityKey = entityType.qualifiedName
        if (!_mappers.contains(entityKey)) {
            logger.error("Mapper not found for $entityKey")
            throw IllegalArgumentException("mapper not found for :$entityKey")
        }

        val mapper = _mappers[entityKey]
        return mapper!!
    }

    fun getModel(entityType: KClass<*>): Model {
        val entityKey = getKey(entityType)
        if (!_info.contains(entityKey)) {
            logger.error("Model not found for $entityKey")
            throw IllegalArgumentException("model not found for: " + entityType.qualifiedName)
        }

        return _info.get(entityKey)?.model!!
    }

    fun getDb(dbKey: String = "", dbShard: String = ""): Db {
        val err1 = "Error getting database for registration in Entities."
        val err2 = "Database connection not setup and/or available in config."
        val err3 = "Database connection not setup and/or available in config for $dbKey & $dbShard."
        val err = if (dbKey.isNullOrEmpty()) err2 else err3
        val con = getDbCon()
        if (con == null) {
            logger.error("Database connection not available for key/shard $dbKey:$dbShard")
        }
        require(con != null, { err1 + " " + err })
        require(con != DbConEmpty, { err1 + " " + err })
        return Db(con!!, errorCallback = this::errorHandler).open()
    }

    fun getDbCon(dbKey: String = "", dbShard: String = ""): DbCon? {
        return _dbs?.let { dbs ->

            // Case 1: default connection
            if (dbKey.isNullOrEmpty())
                dbs.default()

            // Case 2: named connection
            else if (dbShard.isNullOrEmpty())
                dbs.named(dbKey)

            // Case 3: shard
            else
                dbs.group(dbKey, dbShard)
        }
    }

    fun getInfo(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityInfo {
        val key = getKey(entityType, dbKey, dbShard)
        require(_info.contains(key), { "Entity invalid or not registered with key : " + key })
        return _info.get(key)!!
    }

    fun getEntities(): List<EntityInfo> = _info.all()

    fun getInfoByName(entityType: String, dbKey: String = "", dbShard: String = ""): EntityInfo {
        val key = buildKey(entityType, dbKey, dbShard)
        if (!_info.contains(key)) {
            logger.error("Mapper not found for $key")
            throw IllegalArgumentException("invalid entity : $key")
        }
        return _info.get(key)!!
    }

    private fun getKey(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): String =
            buildKey(entityType.qualifiedName!!, dbKey, dbShard)

    private fun buildKey(entityType: String, dbKey: String = "", dbShard: String = ""): String =
            "$entityType:$dbKey:$dbShard"

    private fun buildMapper(isSqlRepo: Boolean, entityType: KClass<*>, model: Model?, mapper: EntityMapper?, tableName: String?): EntityMapper {

        val entityKey = entityType.qualifiedName

        fun createMapper(entityType: KClass<*>): EntityMapper {
            val entityModel = model ?: ModelMapper.loadSchema(entityType, namer = namer)
            val em = EntityMapper(entityModel, encryptor = _enc, namer = namer)
            return em
        }

        val entityMapper = mapper ?: createMapper(entityType)
        _mappers.put(entityKey!!, entityMapper)
        return entityMapper
    }

    private fun <T> buildRepo(
        isSqlRepo: Boolean,
        dbType: DbType,
        dbKey: String,
        dbShard: String,
        entityType: KClass<*>,
        mapper: EntityMapper,
        tableName: String?
    ): EntityRepo<T> where T : Entity {
        // Currently only long supported
        val entityIdType = Long::class
        val repoType = if (!isSqlRepo) DbTypeMemory else dbType
        val repo = when (repoType) {

            DbTypeMemory -> {
                EntityRepoInMemory<T>(entityType, entityIdType, mapper)
            }
            DbTypeMySql -> {
                EntityRepoMySql<T>(getDb(dbKey, dbShard), entityType, entityIdType, mapper, tableName, _enc)
            }
            else -> {
                EntityRepoInMemory<T>(entityType, entityIdType, mapper)
            }
        }
        return repo
    }

    private fun buildDDL(isSqlRepo: Boolean, dbType: DbType): EntityDDL? {
        // Currently only long supported
        val repoType = if (!isSqlRepo) DbTypeMemory else dbType
        val ddl = when (repoType) {
            DbTypeMySql -> MySqlEntityDDL()
            else -> null
        }
        return ddl
    }

    private fun <T> buildService(
        serviceType: KClass<*>?,
        repo: EntityRepo<T>,
        ctx: Any?
    ): EntityService<T> where T : Entity {

        val service = serviceType?.let { stype ->

            // Parameters to service is the context and repo
            val params = ctx?.let { args ->
                listOf(args, this, repo)
            } ?: listOf(this, repo)

            Reflector.createWithArgs<EntityService<T>>(stype, params.toTypedArray())
        } ?: EntityService(this, repo)
        return service
    }

    fun errorHandler(ex: Exception) {
        logger.error("Database error", ex)
    }
}

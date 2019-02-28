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
import slatekit.db.DbType.DbTypeMySql
import slatekit.db.DbType.DbTypePGres
import slatekit.db.types.DbSource
import slatekit.db.types.DbSourceMySql
import slatekit.db.types.DbSourcePostGres
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.db.Db
import slatekit.db.DbType
import slatekit.entities.repos.EntityMapperInMemory
import slatekit.entities.repos.EntityRepoInMemory
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
open class Entities<TInfo>(
        _dbs: DbLookup? = DbLookup.defaultDb(DbCon.empty),
        val enc: Encryptor? = null,
        val logs: Logs = LogsDefault,
        val namer: Namer? = null
) where TInfo:EntityInfo {

    protected var _info = ListMap<String, TInfo>(listOf())
    protected val _mappers = mutableMapOf<String, EntityMapper<*,*>>()
    protected val logger = logs.getLogger("db")
    open val builder = EntityBuilder(_dbs, enc)

    open fun <TId, T> register(
            entityType: KClass<*>
    ): TInfo where TId:Comparable<TId>, T:Entity<TId> {
        val mapper = EntityMapperInMemory<TId, T>()
        return register(entityType,
                EntityRepoInMemory(entityType, Long::class, mapper),
                mapper, DbType.DbTypeMemory)
    }


    open fun <TId, T> register(
            entityType: KClass<*>,
            repo: EntityRepo<TId, T>,
            mapper: EntityMapper<TId, T>,
            dbType: DbType,
            model:Model? = null,
            serviceType: KClass<*>? = null,
            dbKey: String? = null,
            dbShard: String? = null,
            tableName: String? = null,
            serviceCtx: Any? = null,
            persistUTC: Boolean = false
    ): TInfo where TId:Comparable<TId>, T:Entity<TId> {

        // 1. Service ( used to provide validation, placeholder for business functionality )
        val service = builder.service(this, serviceType, repo, serviceCtx)
        val serviceTypeFinal = serviceType ?: service::class

        // 2. Now store all the info for easy lookup
        val info = EntityInfo(
                entityType,
                serviceTypeFinal,
                repo::class,
                repo,
                mapper,
                dbType,
                dbKey ?: "",
                dbShard ?: "",
                service
        ) as TInfo

        val key = builder.key(entityType, dbKey ?: "", dbShard ?: "")
        _info = _info.add(key, info)
        _mappers.put(entityType.qualifiedName!!, mapper)
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
     * Gets a registered mapper for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <TId, T> getMapper(entityType: KClass<*>): EntityMapper<TId,T> where TId:Comparable<TId>, T:Entity<TId> {
        val entityKey = entityType.qualifiedName
        if (!_mappers.contains(entityKey)) {
            logger.error("Mapper not found for $entityKey")
            throw IllegalArgumentException("mapper not found for :$entityKey")
        }

        val mapper = _mappers[entityKey]
        return mapper as EntityMapper<TId, T>
    }

    /**
     * Get a registered repository for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <TId, T> getRepo(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityRepo<TId, T> where TId:Comparable<TId>, T:Entity<TId> =
            getRepoByType(tpe, dbKey, dbShard) as EntityRepo<TId, T>

    /**
     * Get a registered service for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <TId, T> getSvc(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityService<TId, T> where TId:Comparable<TId>, T : Entity<TId> =
            getSvcByType(tpe, dbKey, dbShard) as EntityService<TId, T>


    fun getInfo(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): TInfo {
        val key = builder.key(entityType, dbKey, dbShard)
        require(_info.contains(key), { "Entity invalid or not registered with key : " + key })
        return _info.get(key)!!
    }

    fun getInfoByName(entityType: String, dbKey: String = "", dbShard: String = ""): TInfo {
        val key = builder.key(entityType, dbKey, dbShard)
        if (!_info.contains(key)) {
            logger.error("Mapper not found for $key")
            throw IllegalArgumentException("invalid entity : $key")
        }
        return _info.get(key)!!
    }

    fun getSvcByTypeName(entityType: String, dbKey: String = "", dbShard: String = ""): IEntityService {
        val info = getInfoByName(entityType, dbKey, dbShard)
        return info.entityServiceInstance!!
    }


    private fun getSvcByType(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): IEntityService {
        val info = getInfo(entityType, dbKey, dbShard)
        return info.entityServiceInstance!!
    }


    private fun getRepoByType(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): IEntityRepo {
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

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

package slatekit.entities

import slatekit.common.utils.ListMap
import slatekit.common.naming.Namer
import slatekit.common.db.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.entities.core.*
import slatekit.entities.repos.EntityMapperEmpty
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.entities.repos.IdGenerator
import slatekit.entities.repos.LongIdGenerator
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
open class Entities(
        dbCreator: (DbCon) -> IDb,
        val dbs: DbLookup = DbLookup.defaultDb(DbCon.empty),
        val enc: Encryptor? = null,
        val logs: Logs = LogsDefault,
        val namer: Namer? = null
)  {

    private var info = ListMap<String, EntityContext>(listOf())
    val mappers = mutableMapOf<String, EntityMapper<*, *>>()
    val logger = logs.getLogger("db")
    open val builder = EntityBuilder(dbCreator, dbs, enc)


    /**
     * Register the entity using a pre-built [EntityContext] object
     * which contains all the relevant info about an Entity, its id,
     * and its corresponding mapper, repo, service, etc.
     */
    open fun register(ctx: EntityContext) {
        val key = builder.key(ctx.entityType, "", "")
        info = info.add(key, ctx)
        mappers[ctx.entityType.qualifiedName!!] = ctx.entityMapperInstance
    }


    /**
     * Register the entity using a pre-built [EntityContext] object
     * which contains all the relevant info about an Entity, its id,
     * and its corresponding mapper, repo, service, etc.
     */
    open fun <TId, T> register(
            entityType: KClass<*>,
            entityIdType: KClass<*>,
            service: EntityService<TId, T>,
            dbType:DbType): EntityContext where TId : Comparable<TId>, T : Entity<TId> {
        val mapper = EntityMapperEmpty<TId, T>(null)
        val context = EntityContext(entityType, entityIdType, service::class, service.repo(), mapper, dbType, Model(entityType), "", "", service)
        register(context)
        return context
    }


    /**
     * Register the entity using a pre-built [EntityContext] object
     * which contains all the relevant info about an Entity, its id,
     * and its corresponding mapper, repo, service, etc.
     */
    open fun <TId, T> register(
            entityType: KClass<*>,
            entityIdType: KClass<*>,
            serviceType:KClass<*>,
            repo: EntityRepo<TId, T>,
            mapper: EntityMapper<TId, T>?,
            dbType:DbType,
            serviceCtx:Any? = null): EntityContext where TId : Comparable<TId>, T : Entity<TId> {
        val service = builder.service(this, serviceType, repo, serviceCtx)
        val finalMapper = mapper ?: EntityMapperEmpty(null)
        val context = EntityContext(entityType, entityIdType, serviceType, repo, finalMapper, dbType, Model(entityType), "", "", service, serviceCtx)
        register(context)
        return context
    }


    /**
     * Register a Entity with a Long Id, and an In-Memory Repository for prototyping / testing purposes
     * WARNING!!! : This should only be used for Domain Driven prototyping, mocking, testing purposes
     *
     * @param entityType   :  Type of the Entity / Domain class ( e.g. User )
     */
    open fun <T> prototype(entityType: KClass<*>,
                           serviceType:KClass<*>? = null,
                           serviceCtx:Any? = null,
                           loadSchema:Boolean = false): EntityContext where T: Entity<Long> {
        return this.prototype<Long, T>(entityType, Long::class, LongIdGenerator(), loadSchema = loadSchema, serviceType = serviceType, serviceCtx = serviceCtx)
    }


    /**
     * Register a Entity with a In-Memory Repository for prototyping / testing purposes
     * WARNING!!! : This should only be used for Domain Driven prototyping, mocking, testing purposes
     *
     * @param entityType   :  Type of the Entity / Domain class ( e.g. User )
     * @param entityIdType :  Type of the id of the Entity / Domain class ( e.g. Long )
     * @param entityIdGen  :  Id generator for primary keys / ids for the In-Memory repository
     * @param tableName    :  Optional name of the database table for entityt ( defaults to class name e.g. "user" )
     * @param serviceType  :  Optional type of the [EntityService] to create instance
     * @param serviceCtx   :  Context info to pass to the [EntityService] type for creation
     */
    open fun <TId, T> prototype(
            entityType: KClass<*>,
            entityIdType: KClass<*>,
            entityIdGen: IdGenerator<TId>,
            loadSchema: Boolean,
            tableName: String? = null,
            serviceType: KClass<*>? = null,
            serviceCtx: Any? = null): EntityContext where TId : Comparable<TId>, T : Entity<TId> {

        val table = buildTableName(entityType, tableName, namer)

        // 1. Model ( schema of the entity which maps fields to columns and has other metadata )
        val model = if(loadSchema) ModelMapper.loadSchema(entityType) else Model(entityType, table) // Empty model as this is in-memory

        // 2. Mapper ( maps entities to/from sql using the model/schema )
        val mapper = EntityMapperEmpty<TId, T>(model) // Empty mapper as this is in-memory

        // 3. Repo ( provides CRUD using the Mapper)
        val info = EntityInfo(entityIdType, entityType, table, '`', model, this.enc, this.namer)
        val repo = EntityRepoInMemory(info, entityIdGen)

        // 4. Service ( used to provide validation, placeholder for business functionality )
        val service = builder.service(this, serviceType, repo, serviceCtx)

        // 5. Capture the actual service type ( could be the default EntityService implementation )
        val svcType = service::class

        // 6. Entity context captures all relevant info about a mapped Entity( id type, entity type, etc. )
        val context = EntityContext(entityType, entityIdType, svcType, repo, mapper, DbType.DbTypeMemory, model, "", "",
                service, serviceCtx)

        // 7. Finally Register
        this.register(context)

        return context
    }


    /**
     * Gets the default database
     */
    fun getDb(): IDb = builder.db()


    /**
     * Gets a database by its name/alias
     */
    fun getDbByName(name: String): IDb = builder.db(name)


    /**
     * Gets a list of all the registered entities
     */
    fun getEntities(): List<EntityContext> = info.all()


    /**
     * Gets a registered mapper for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <TId, T> getMapper(entityType: KClass<*>): EntityMapper<TId, T> where TId:Comparable<TId>, T: Entity<TId> {
        val entityKey = entityType.qualifiedName
        if (!mappers.contains(entityKey)) {
            logger.error("Mapper not found for $entityKey")
            throw IllegalArgumentException("mapper not found for :$entityKey")
        }

        val mapper = mappers[entityKey]
        return mapper as EntityMapper<TId, T>
    }

    /**
     * Get a registered repository for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <TId, T> getRepo(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityRepo<TId, T> where TId:Comparable<TId>, T: Entity<TId> =
            getRepoByType(tpe, dbKey, dbShard) as EntityRepo<TId, T>

    /**
     * Get a registered service for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <TId, T> getSvc(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityService<TId, T> where TId:Comparable<TId>, T : Entity<TId> =
            getSvcByType(tpe, dbKey, dbShard) as EntityService<TId, T>


    fun getInfo(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityContext {
        val key = builder.key(entityType, dbKey, dbShard)
        return getInfoByKey(key)
    }

    fun getInfoByKey(key:String): EntityContext {
        val ctx = info[key]
        return ctx ?: throw Exception("Entity invalid or not registered with key : $key")
    }

    fun getInfoByName(entityType: String, dbKey: String = "", dbShard: String = ""): EntityContext {
        val key = builder.key(entityType, dbKey, dbShard)
        return getInfoByKey(key)
    }

    fun getSvcByTypeName(entityType: String, dbKey: String = "", dbShard: String = ""): GenericService {
        val info = getInfoByName(entityType, dbKey, dbShard)
        return info.entityServiceInstance ?: throw Exception("Entity service not available")
    }


    fun getSvcByType(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): GenericService {
        val info = getInfo(entityType, dbKey, dbShard)
        return info.entityServiceInstance ?: throw Exception("Entity service not available")
    }


    private fun getRepoByType(tpe: KClass<*>, dbKey: String = "", dbShard: String = ""): EntityStore {
        val info = getInfo(tpe, dbKey, dbShard)
        return info.entityRepoInstance
    }

}

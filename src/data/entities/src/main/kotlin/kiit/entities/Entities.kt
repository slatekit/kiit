/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.entities

import kotlin.reflect.KClass
import kiit.common.data.*
import kiit.common.crypto.Encryptor
import kiit.common.log.Logs
import kiit.common.log.LogsDefault
import kiit.utils.naming.Namer
import kiit.common.values.ListMap
import kiit.data.core.*
import kiit.data.encoders.Encoders
import kiit.data.sql.vendors.*
import kiit.entities.core.*
import kiit.entities.mapper.EntityMapper
import kiit.entities.mapper.EntitySettings
import kiit.meta.kClass
import kiit.meta.models.Model

/**
 *  A registry for all the entities and their corresponding services, repositories, database
 *  types, and connection keys.
 *  val entities = Entities(...)
 *  entities.register<Long, User5>(EntityLongId(), vendor = Vendor.MySql) { repo -> UserService(repo) }
 *
 *  // Case 1: In-memory
 *  entities.register<Long, Invitation>(EntityLongId(), vendor = Vendor.MySql)
 *
 *  // Case 2: In-memory, with custom service
 *  entities.register<Long, Invitation>(EntityLongId(), vendor = Vendor.MySql, serviceType: typeof(InvitationService));
 *
 *  // Case 3: Sql-repo
 *  entities.register<Long, Invitation>(EntityLongId(), sqlRepo: true)
 *
 *  // Case 4: Sql-repo, with custom service
 *  entities.register<Long, Invitation>(EntityLongId(), vendor = Vendor.MySql, serviceType: typeof(InvitationService));
 *
 *  // Case 5: Custom repository
 *  entities.register<Long, Invitation>(EntityLongId(), vendor = Vendor.MySql, repo: InvitationRepository());
 *
 *  // Case 6: Custom repo with provider type specified
 *  entities.register<Long, Invitation>(EntityLongId(), vendor = Vendor.MySql, repo: InvitationRepository(),
 *    vendor: "mysql");
 *
 *  // Case 7: Full customization
 *  entities.register<Long, Invitation>(EntityLongId(), vendor = Vendor.MySql, serviceType: typeof(InvitationService),
 *    repo: InvitationRepository(), vendor: "mysql");
 *
 *  // Case 8: Full customization
 *  entities.register<Long, Invitation>(EntityLongId(), vendor = Vendor.MySql, serviceType: typeof(InvitationService),
 *     repo: InvitationRepository(), mapper: null, vendor: "mysql");
 *
 */
open class Entities(
    dbCreator: (DbCon) -> IDb,
    val dbs: Connections = Connections.of(DbCon.empty),
    val enc: Encryptor? = null,
    val logs: Logs = LogsDefault,
    val namer: Namer? = null
) {
    private var info = ListMap<String, EntityContext>(listOf())
    val mappers = mutableMapOf<String, EntityMapper<*, *>>()
    val logger = logs.getLogger("db")
    open val builder = EntityBuilder(dbCreator, dbs, enc)

    /**
     * Register the entity using a pre-built [EntityContext] object
     * which contains all the relevant info about an Entity, its id,
     * and its corresponding mapper, repo, service, etc.
     */
    /*
    inline fun <reified TId, reified T> register(idOps:kiit.data.core.Id<TId, T>,
                                                 table: String? = null,
                                                 vendor: Vendor = Vendor.MySql,
                                                 builder: (EntityRepo<TId, T>) -> EntityService<TId, T>) where TId : Comparable<TId>, T : Entity<TId> {
        // 1. Id/Model types e.g. Long / User
        val idName = idOps.name()
        val idType = TId::class
        val idTypeJ = idType.javaPrimitiveType!!
        val enType = T::class

        // 2. Table info ( name of table supplied or use class name )
        val tableChar = when(vendor){
            Vendor.Postgres  -> PostgresDialect.encodeChar
            Vendor.MySql     -> MySqlDialect.encodeChar
            Vendor.SqLite    -> SqliteDialect.encodeChar
            Vendor.H2        -> H2Dialect.encodeChar
            Vendor.Memory    -> MySqlDialect.encodeChar
        }
        val tableName = table ?: enType.simpleName!!
        val tableKey = PKey(idName, DataType.fromJava(idTypeJ))
        val tableInfo = Table(tableName, tableChar, tableKey)

        // 3. Schema / Meta data
        val entityModel = Schema.load(enType, idName, null, tableName)
        val entityMeta = Meta<TId, T>(idOps, tableInfo)

        // 4. Mapper
        val encoders = when(vendor){
            Vendor.SqLite -> SqliteEncoders<TId, T>(true)
            else -> Encoders<TId, T>()
        }
        val entityMapper = EntityMapper<TId, T>(entityModel, entityMeta, idType, enType, EntitySettings(true), encoders)
        val provider = when(vendor){
            Vendor.Postgres -> PostgresProvider(entityMeta, entityMapper)
            Vendor.MySql    -> MySqlProvider(entityMeta, entityMapper)
            Vendor.SqLite   -> SqliteProvider(entityMeta, entityMapper)
            Vendor.H2       -> H2Provider(entityMeta, entityMapper)
            Vendor.Memory   -> MySqlProvider(entityMeta, entityMapper)
        }
        val entityRepo = EntityRepo<TId, T>(getDb(), entityMeta, entityMapper, provider)
        val entityService = builder(entityRepo)

        // 5. Context has all relevant info
        val entityServiceType = entityService.kClass
        val entityContext = EntityContext(enType, idType, entityService, entityRepo, entityMapper, vendor, entityModel, "", "")
        register(entityContext)
    }
    */

    /**
     * Register the entity using a pre-built [EntityContext] object
     * which contains all the relevant info about an Entity, its id,
     * and its corresponding mapper, repo, service, etc.
     */
    inline fun <reified TId, reified T> register(idOps:kiit.data.core.Id<TId, T>,
                                                 table: String? = null, schema: String? = null,
                                                 vendor: Vendor = Vendor.MySql,
                                                 builder: (EntityRepo<TId, T>) -> EntityService<TId, T>) where TId : Comparable<TId>, T : Entity<TId> {
        // 1. Id/Model types e.g. Long / User
        val idName = idOps.name()
        val idType = TId::class
        val enType = T::class

        val entityRepo:EntityRepo<TId, T> = repo(idOps, idType, enType, table, schema, vendor)
        val entityMapper:EntityMapper<TId, T> = entityRepo.mapper as EntityMapper<TId, T>
        val entityModel:Model = entityMapper.model
        val entityService = builder(entityRepo)

        // 5. Context has all relevant info
        val entityServiceType = entityService.kClass
        val entityContext = EntityContext(enType, idType, entityService, entityRepo, entityMapper, vendor, entityModel, "", "")
        register(entityContext)
    }


    fun <TId, T> repo(idOps:kiit.data.core.Id<TId, T>, idType:KClass<TId>, enType:KClass<T>,
                                             table: String? = null, schema: String? = null,
                                             vendor: Vendor = Vendor.MySql) : EntityRepo<TId, T>
        where TId : Comparable<TId>, T : Entity<TId> {

        // 1. Id/Model types e.g. Long / User
        val idName = idOps.name()
        val idTypeJ = idType.javaPrimitiveType!!

        // 2. Table info ( name of table supplied or use class name )
        val tableChar = when(vendor){
            Vendor.Postgres  -> PostgresDialect.encodeChar
            Vendor.MySql     -> MySqlDialect.encodeChar
            Vendor.SqLite    -> SqliteDialect.encodeChar
            Vendor.H2        -> H2Dialect.encodeChar
            Vendor.Memory    -> MySqlDialect.encodeChar
        }
        val tableName = table ?: enType.simpleName!!
        val tableKey = PKey(idName, DataType.fromJava(idTypeJ))
        val tableInfo = Table(tableName, tableChar, tableKey, schema ?: "")

        // 3. Schema / Meta data
        val entityModel = Schema.load(enType, idName, null, tableName)
        val entityMeta = Meta<TId, T>(idOps, tableInfo)

        // 4. Mapper
        val encoders = when(vendor){
            Vendor.SqLite -> SqliteEncoders<TId, T>(true)
            else -> Encoders<TId, T>()
        }
        val entityMapper = EntityMapper<TId, T>(entityModel, entityMeta, idType, enType, EntitySettings(true), encoders)
        val provider = when(vendor){
            Vendor.Postgres -> PostgresProvider(entityMeta, entityMapper)
            Vendor.MySql    -> MySqlProvider(entityMeta, entityMapper)
            Vendor.SqLite   -> SqliteProvider(entityMeta, entityMapper)
            Vendor.H2       -> H2Provider(entityMeta, entityMapper)
            Vendor.Memory   -> MySqlProvider(entityMeta, entityMapper)
        }
        val entityRepo = EntityRepo<TId, T>(getDb(), entityMeta, entityMapper, provider)
        return entityRepo
    }

    /**
     * Register the entity using a pre-built [EntityContext] object
     * which contains all the relevant info about an Entity, its id,
     * and its corresponding mapper, repo, service, etc.
     */
    fun register(ctx: EntityContext) {
        val key = builder.key(ctx.entityType, "", "")
        info = info.add(key, ctx)
        mappers[ctx.entityType.qualifiedName!!] = ctx.entityMapperInstance
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
    fun <TId, T> getMapper(entityType: KClass<*>): EntityMapper<TId, T> where TId : Comparable<TId>, T : Entity<TId> {
        val entityKey = entityType.qualifiedName
        if (!mappers.contains(entityKey)) {
            logger.error("Mapper not found for $entityKey")
            throw IllegalArgumentException("mapper not found for :$entityKey")
        }

        val mapper = mappers[entityKey]
        return mapper as EntityMapper<TId, T>
    }

    /**
     * Get a registered Entity repository for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    fun <TId, T> getRepo(cls:KClass<T>): EntityRepo<TId, T> where TId : Comparable<TId>, T : Entity<TId> =
        getRepoByType(cls) as EntityRepo<TId, T>

    /**
     * Get a registered Entity repository for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified TId, reified T> getRepo(): EntityRepo<TId, T> where TId : Comparable<TId>, T : Entity<TId> =
        getRepoByType(T::class) as EntityRepo<TId, T>

    /**
     * Get a registered Entity service for the entity type
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified TId, reified T> getService(): EntityService<TId, T> where TId : Comparable<TId>, T : Entity<TId> =
        getServiceByType(T::class) as EntityService<TId, T>

    /**
     * Gets the model tied to the entity type T
     */
    inline fun <reified T> getModel(): Model {
        return getInfoByName(T::class.qualifiedName!!).model
    }

    /**
     * Gets the model tied to the entity type T
     */
    inline fun getModel(cls:KClass<*>): Model {
        return getModel(cls.qualifiedName!!)
    }


    inline fun getModel(qualifiedName:String): Model {
        return getInfoByName(qualifiedName).model
    }

    fun getInfoByName(entityType: String): EntityContext {
        val key = builder.key(entityType)
        return getInfoByKey(key)
    }

    fun getServiceByType(entityType: KClass<*>): EntityService<*, *> {
        val info = getInfoInternal(entityType)
        return info.entityServiceInstance ?: throw Exception("Entity service not available")
    }

    fun getServiceByTypeName(entityType: String): EntityService<*, *> {
        val info = getInfoByName(entityType)
        return info.entityServiceInstance ?: throw Exception("Entity service not available")
    }

    fun getRepoByType(tpe: KClass<*>): EntityRepo<*, *> {
        val info = getInfoInternal(tpe)
        return info.entityRepoInstance
    }

    inline fun <reified T> getInfo(): EntityContext? {
        return getInfoByName(T::class.qualifiedName!!)
    }

    private fun getInfoInternal(entityType: KClass<*>): EntityContext {
        val key = builder.key(entityType)
        return getInfoByKey(key)
    }

    private fun getInfoByKey(key: String): EntityContext {
        val ctx = info[key]
        return ctx ?: throw Exception("Entity invalid or not registered with key : $key")
    }
}

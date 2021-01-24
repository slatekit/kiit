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

import kotlin.reflect.KClass
import slatekit.common.data.*
import slatekit.common.crypto.Encryptor
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.naming.Namer
import slatekit.common.utils.ListMap
import slatekit.data.core.*
import slatekit.entities.core.*
import slatekit.data.syntax.SqlSyntax
import slatekit.entities.mapper.EntityMapper
import slatekit.entities.mapper.EntitySettings
import slatekit.meta.kClass
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper

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
 *     vendor: "mysql");
 *
 *   // Case 7: Full customization
 *   Entities.register[Invitation](sqlRepo: true, serviceType: typeof(InvitationService),
 *     repo: InvitationRepository(), vendor: "mysql");
 *
 *   // Case 8: Full customization
 *   Entities.register[Invitation](sqlRepo: true, serviceType: typeof(InvitationService),
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
    inline fun <reified TId, reified T> register(idOps:Id<TId, T>,
                                                 table: String? = null,
                                                 vendor: Vendor = Vendor.MySql,
                                                 builder: (EntityRepo<TId, T>) -> EntityService<TId, T>) where TId : Comparable<TId>, T : Entity<TId> {
        // 1. Id/Model types e.g. Long / User
        val idName = idOps.name()
        val idType = TId::class
        val enType = T::class

        // 2. Table info ( name of table supplied or use class name )
        val tableName = table ?: enType.simpleName!!
        val tableKey = PKey(idName, DataType.getTypeFromLang(idType.java))
        val tableChar = if(vendor == Vendor.MySql) '`' else '"'
        val tableInfo = Table(tableName, tableChar, tableKey)

        // 3. Schema / Meta data
        val entityModel = ModelMapper.loadSchema(enType, idName, null, tableName)
        val entityMeta = Meta<TId, T>(idOps, tableInfo)
        val entityInfo = EntityInfo(idType, enType, entityMeta, entityModel)

        // 4. Mapper
        val entityMapper = EntityMapper<TId, T>(entityModel, entityMeta, idType, enType, EntitySettings(true))
        val entityRepo = EntitySqlRepo<TId, T>(getDb(), entityInfo, entityMeta, entityMapper, SqlSyntax(entityMeta, entityMapper))
        val entityService = builder(entityRepo)

        // 5. Context has all relevant info
        val entityServiceType = entityService.kClass
        val entityContext = EntityContext(enType, idType, entityService, entityRepo, entityMapper, vendor, entityModel, "", "")
        register(entityContext)
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

    fun getInfoByName(entityType: String): EntityContext {
        val key = builder.key(entityType)
        return getInfoByKey(key)
    }

    fun getServiceByType(entityType: KClass<*>): EntityService<*, *> {
        val info = getInfo(entityType)
        return info.entityServiceInstance ?: throw Exception("Entity service not available")
    }

    fun getServiceByTypeName(entityType: String): EntityService<*, *> {
        val info = getInfoByName(entityType)
        return info.entityServiceInstance ?: throw Exception("Entity service not available")
    }

    fun getRepoByType(tpe: KClass<*>): EntityRepo<*, *> {
        val info = getInfo(tpe)
        return info.entityRepoInstance
    }

    private fun getInfo(entityType: KClass<*>): EntityContext {
        val key = builder.key(entityType)
        return getInfoByKey(key)
    }

    private fun getInfoByKey(key: String): EntityContext {
        val ctx = info[key]
        return ctx ?: throw Exception("Entity invalid or not registered with key : $key")
    }
}

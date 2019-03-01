package slatekit.entities.core

import slatekit.common.db.*
import slatekit.common.encrypt.Encryptor
import slatekit.meta.Reflector
import slatekit.common.naming.Namer
//import slatekit.db.Db
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper
import kotlin.reflect.KClass

/**
 * Responsible for building individual components of this Micro-ORM.
 * 1. Con     : Database connection Info
 * 2. Db      : JDBC wrapper ( depends on Con above )
 * 3. Model   : Schema ( columns ) of entity that can be persisted
 * 4. Mapper  : Maps entities to/from sql
 * 5. Repo    : Repository implementation ( depends on Db above )
 * 6. Service : Service implementation ( depends on Repo above )
 */
open class EntityBuilder(
        val dbCreator: (DbCon) -> IDb,
        val dbs: DbLookup? = null,
        val enc: Encryptor? = null) {


    /**
     * Builds the unique key for looking up an entity by its name, database key and shard.
     * @param entityType: The class name e.g. "MyApp.User"
     * @param dbKey: The name of the database key ( empty / "" by default if only 1 database )
     * @param dbShard: The name of the database shard ( empty / "" by default if only 1 database )
     */
    fun key(entityType: KClass<*>, dbKey: String = "", dbShard: String = ""): String =
            key(entityType.qualifiedName!!, dbKey, dbShard)


    /**
     * Builds the unique key for looking up an entity by its name, database key and shard.
     * @param entityType: The class name e.g. "MyApp.User" ( qualified name )
     * @param dbKey: The name of the database key ( empty / "" by default if only 1 database )
     * @param dbShard: The name of the database shard ( empty / "" by default if only 1 database )
     */
    fun key(entityType: String, dbKey: String = "", dbShard: String = ""): String = "$entityType:$dbKey:$dbShard"


    /**
     * Builds the database connection based on the key/shard provided.
     * @param dbKey: The name of the database key ( empty / "" by default if only 1 database )
     * @param dbShard: The name of the database shard ( empty / "" by default if only 1 database )
     */
    fun con(dbKey: String = "", dbShard: String = ""): DbCon? {
        return dbs?.let { dbs ->
            when {
                // Case 1: default connection
                dbKey.isEmpty() -> dbs.default()

                // Case 2: named connection
                dbShard.isEmpty() -> dbs.named(dbKey)

                // Case 3: shard
                else -> dbs.group(dbKey, dbShard)
            }
        }
    }


    /**
     * Builds the database by loading up the connection info for the database key / shard provided.
     * @param dbKey: The name of the database key ( empty / "" by default if only 1 database )
     * @param dbShard: The name of the database shard ( empty / "" by default if only 1 database )
     */
    fun db(dbKey: String = "", dbShard: String = "", open: Boolean = true): IDb {
        val err1 = "Error getting database for registration in Entities."
        val err2 = "Database connection not setup and/or available in config."
        val err3 = "Database connection not setup and/or available in config for $dbKey & $dbShard."
        val err = if (dbKey.isEmpty()) err2 else err3
        val con = con()
                ?: throw IllegalArgumentException("Database connection not available for key/shard $dbKey:$dbShard")
        require(con != DbCon.empty, { "$err1 $err" })
        return if (open) dbCreator(con).open() else dbCreator(con)
    }


    /**
     * Builds a Model representing the schema of the Entity ( using the Class name )
     * NOTE: Currently the system enforces an primary key be named as "id"
     * This may be removed later
     */
    fun model(entityType: KClass<*>, namer: Namer?, table:String?): Model {
        return ModelMapper.loadSchema(entityType, EntityWithId<*>::id.name, namer, table)
    }


    /**
     * Dynamically builds an EntityService using the type and repo supplied.
     * @param entities: The top level Entities object to get instances of various types
     * @param serviceType: The KClass of the EntityService implementation to create
     * @param repo: The underlying EntityRepo that handle persistence
     * @param args: Additional arguments to constructor of service during creation
     */
    fun <TId, T> service(
            entities: Entities<*>,
            serviceType: KClass<*>?,
            repo: EntityRepo<TId, T>,
            args: Any?
    ): EntityService<TId,T> where TId:Comparable<TId>, T : Entity<TId> {
        return serviceType?.let {
            // Parameters to service is the context and repo
            val params = args?.let { args -> listOf(args, entities, repo) } ?: listOf(entities, repo)
            Reflector.createWithArgs<EntityService<TId, T>>(it, params.toTypedArray())
        } ?: EntityService(entities, repo)
    }
}

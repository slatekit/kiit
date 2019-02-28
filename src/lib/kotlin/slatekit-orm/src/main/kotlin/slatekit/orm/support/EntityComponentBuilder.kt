package slatekit.orm.support

import slatekit.common.db.*
import slatekit.common.encrypt.Encryptor
import slatekit.orm.core.*
import slatekit.orm.repos.EntityRepoInMemory
import slatekit.meta.Reflector
import slatekit.db.DbType.*
import slatekit.common.naming.Namer
import slatekit.db.Db
import slatekit.db.DbType
import slatekit.orm.databases.SqlBuilder
import slatekit.orm.databases.vendors.*
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
class EntityComponentBuilder(val dbs: DbLookup? = null,
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
    fun db(dbKey: String = "", dbShard: String = "", open: Boolean = true): Db {
        val err1 = "Error getting database for registration in Entities."
        val err2 = "Database connection not setup and/or available in config."
        val err3 = "Database connection not setup and/or available in config for $dbKey & $dbShard."
        val err = if (dbKey.isEmpty()) err2 else err3
        val con = con()
                ?: throw IllegalArgumentException("Database connection not available for key/shard $dbKey:$dbShard")
        require(con != DbCon.empty, { "$err1 $err" })
        return if (open) Db(con).open() else Db(con)
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
     * Builds Sql builder used for creating the schema/ddl statements for setup/migration
     * @param dbType: The type of the database to create
     * @param namer: Optional namer to create naming conventions
     */
    fun ddl(dbType: DbType, namer:Namer?): SqlBuilder {
        return when (dbType) {
            DbTypeMySql -> MySqlBuilder(namer)
            DbTypePGres -> PostGresBuilder(namer)
            else -> SqlBuilder(MySqlTypeMap, namer)
        }
    }


    /**
     * Builds the database by loading up the connection info for the database key / shard provided.
     * @param dbType: The type of the database to create
     * @param model: The schema of the Entity represented as a Model definition
     * @param tableName: Optional name of the table for the model if different than entity name
     * @param utc: Optional flag to save all datetimes in UTC ( defaults to false )
     * @param enc: Optional encrptor to support encryption of selected columns
     * @param namer: Optional namer to create naming conventions
     */
    fun mapper(dbType: DbType, model: Model, utc: Boolean = false, enc: Encryptor? = null, namer: Namer? = null): EntityMapper {
        return when (dbType) {
            DbTypeMySql -> MySqlEntityMapper(model, utc, enc, namer)
            DbTypePGres -> PostGresEntityMapper(model, utc, enc, namer)
            else -> EntityMapper(model, MySqlConverter, utc, '`', enc, namer)
        }
    }


    /**
     * Builds the repository associated w/ the database type
     * @param entityType: The class name e.g. "MyApp.User" of the Entity
     * @param dbType: The type of the database to create
     * @param dbKey: The name of the database key ( empty / "" by default if only 1 database )
     * @param dbShard: The name of the database shard ( empty / "" by default if only 1 database )
     * @param tableName: Optional name of the table for the model if different than entity name
     * @param utc: Optional flag to save all datetimes in UTC ( defaults to false )
     * @param enc: Optional encrptor to support encryption of selected columns
     * @param namer: Optional namer to create naming conventions
     */
    fun <TId, T> repo(
            dbType: DbType,
            dbKey: String,
            dbShard: String,
            entityType: KClass<*>,
            mapper: EntityMapper,
            tableName: String? = null,
            utc: Boolean = false,
            enc: Encryptor? = null,
            namer: Namer? = null
    ): EntityRepo<TId,T> where TId:Comparable<TId>, T : Entity<TId> {

        // NOTE: Only long primary keys supported for now.
        val entityIdType = Long::class

        // 1. DB: JDBC wrapper
        val db = if(dbType == DbTypeMemory) Db(DbCon.empty) else  db(dbKey, dbShard)

        // 2. Repo: Handles all the CRUD / lookup functionality
        return when (dbType) {
            DbTypeMySql -> MySqlEntityRepo(db, entityType, entityIdType, mapper, tableName, enc, namer)
            DbTypePGres -> PostGresEntityRepo(db, entityType, entityIdType, mapper, tableName, enc, namer)
            else -> EntityRepoInMemory(entityType, entityIdType, mapper, enc, namer)
        }
    }


    /**
     * Builds the repository associated w/ the database type
     * @param entityType: The class name e.g. "MyApp.User" of the Entity
     * @param dbType: The type of the database to create
     * @param dbKey: The name of the database key ( empty / "" by default if only 1 database )
     * @param dbShard: The name of the database shard ( empty / "" by default if only 1 database )
     * @param model: The schema of the Entity represented as a Model definition
     * @param tableName: Optional name of the table for the model if different than entity name
     * @param utc: Optional flag to save all datetimes in UTC ( defaults to false )
     * @param enc: Optional encrptor to support encryption of selected columns
     * @param namer: Optional namer to create naming conventions
     */
    fun <TId, T> repoWithMapper(
            dbType: DbType,
            dbKey: String,
            dbShard: String,
            entityType: KClass<*>,
            model: Model,
            tableName: String? = null,
            utc: Boolean = false,
            enc: Encryptor? = null,
            namer: Namer? = null
    ): EntityRepo<TId,T> where TId:Comparable<TId>, T : Entity<TId> {

        // NOTE: Only long primary keys supported for now.
        val entityIdType = Long::class

        // 1. DB: JDBC wrapper
        val db = db(dbKey, dbShard)

        // 2. Mapper: Dynamically maps item to/from sql
        val mapper = mapper(dbType, model, utc, enc, namer)

        // 3. Repo: Handles all the CRUD / lookup functionality
        return when (dbType) {
            DbTypeMySql -> MySqlEntityRepo(db, entityType, entityIdType, mapper, tableName, enc, namer)
            DbTypePGres -> PostGresEntityRepo(db, entityType, entityIdType, mapper, tableName, enc, namer)
            else -> EntityRepoInMemory(entityType, entityIdType, mapper, enc, namer)
        }
    }

    /**
     * Dynamically builds an EntityService using the type and repo supplied.
     * @param entities: The top level Entities object to get instances of various types
     * @param serviceType: The KClass of the EntityService implementation to create
     * @param repo: The underlying EntityRepo that handle persistence
     * @param args: Additional arguments to constructor of service during creation
     */
    fun <TId, T> service(
            entities: Entities,
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

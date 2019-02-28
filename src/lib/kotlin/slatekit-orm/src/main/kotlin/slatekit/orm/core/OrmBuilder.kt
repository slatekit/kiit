package slatekit.orm.core

import slatekit.common.db.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.db.DbType.*
import slatekit.common.naming.Namer
import slatekit.db.Db
import slatekit.common.db.DbType
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityBuilder
import slatekit.entities.core.EntityMapper
import slatekit.entities.core.EntityRepo
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.orm.databases.SqlBuilder
import slatekit.orm.databases.vendors.*
import slatekit.meta.models.Model
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
class OrmBuilder(dbCreator: (DbCon) -> IDb,
                 dbs: DbLookup? = null,
                 enc: Encryptor? = null) : EntityBuilder(dbCreator, dbs, enc) {

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
    fun <TId, T> mapper(dbType: DbType, model: Model, utc: Boolean = false, enc: Encryptor? = null, namer: Namer? = null)
            : EntityMapper<TId, T> where TId:Comparable<TId>, T:Entity<TId> {
        return when (dbType) {
            DbTypeMySql -> MySqlEntityMapper(model, utc, enc, namer)
            DbTypePGres -> PostGresEntityMapper(model, utc, enc, namer)
            else -> OrmMapper(model, MySqlConverter(), utc, '`', enc, namer)
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
            mapper: EntityMapper<TId, T>,
            tableName: String,
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
            tableName: String,
            utc: Boolean = false,
            enc: Encryptor? = null,
            namer: Namer? = null
    ): EntityRepo<TId, T> where TId:Comparable<TId>, T : Entity<TId> {

        // NOTE: Only long primary keys supported for now.
        val entityIdType = Long::class

        // 1. DB: JDBC wrapper
        val db = db(dbKey, dbShard)

        // 2. Mapper: Dynamically maps item to/from sql
        val mapper = mapper<TId, T>(dbType, model, utc, enc, namer)

        // 3. Repo: Handles all the CRUD / lookup functionality
        return when (dbType) {
            DbTypeMySql -> MySqlEntityRepo(db, entityType, entityIdType, mapper, tableName, enc, namer)
            DbTypePGres -> PostGresEntityRepo(db, entityType, entityIdType, mapper, tableName, enc, namer)
            else -> EntityRepoInMemory(entityType, entityIdType, mapper, enc, namer)
        }
    }
}

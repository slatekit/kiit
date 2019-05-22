package slatekit.orm

import slatekit.common.db.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.db.DbType.*
import slatekit.common.naming.Namer
import slatekit.common.db.DbType
import slatekit.db.Db
import slatekit.entities.Entity
import slatekit.entities.core.EntityBuilder
import slatekit.entities.EntityRepo
import slatekit.entities.core.buildTableName
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.orm.core.SqlBuilder
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
                 dbs: DbLookup,
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
     * Builds the mapper by loading up the connection info for the database key / shard provided.
     * @param dbType: The type of the database to create
     * @param db: The Db wrapper for making database calls
     * @param model: The schema of the Entity represented as a Model definition
     * @param utc: Optional flag to save all datetimes in UTC ( defaults to false )
     * @param enc: Optional encrptor to support encryption of selected columns
     * @param namer: Optional namer to create naming conventions
     */
    fun <TId, T> mapper(dbType: DbType, db:IDb, idType:KClass<*>, model: Model, utc: Boolean = false, enc: Encryptor? = null, namer: Namer? = null)
            : OrmMapper<TId, T> where TId:Comparable<TId>, T: Entity<TId> {
        return when (dbType) {
            DbTypeMySql -> MySqlEntityMapper(model, db, idType, utc, enc, namer)
            DbTypePGres -> PostGresEntityMapper(model, db, idType, utc, enc, namer)
            else        -> OrmMapper(model, db, idType, MySqlConverter(), utc, '`', enc, namer)
        }
    }


    /**
     * Builds the mapper by loading up the connection info for the database key / shard provided.
     * @param dbType: The type of the database to create
     * @param entityType   :  Type of the Entity / Domain class ( e.g. User )
     * @param entityIdType :  Type of the id of the Entity / Domain class ( e.g. Long )
     * @param tableName    :  Optional name of the database table for entityt ( defaults to class name e.g. "user" )
     * @param utc: Optional flag to save all datetimes in UTC ( defaults to false )
     * @param namer: Optional namer to create naming conventions
     */
    fun <TId, T> mapper(dbType: DbType, entityIdType: KClass<*>, entityType: KClass<*>,
                        tableName:String?, utc: Boolean = false, namer: Namer? = null): OrmMapper<TId, T>
            where TId:Comparable<TId>, T: Entity<TId> {
        // 1. Table name
        val table = buildTableName(entityType, tableName, namer)

        // 2. Model ( schema of the entity which maps fields to columns and has other metadata )
        val model = this.model(entityType, namer, table)

        // 3. Connection info ( using default connection )
        val con = this.con()

        // 4. Db ( JDBC database call wrapper with connection )
        val db = this.db( con )

        // 5. Mapper ( maps entities to/from sql using the model/schema )
        val mapper = this.mapper<TId, T>(dbType, db, entityIdType, model, utc, enc, namer)

        return mapper
    }


    /**
     * Builds the repository associated w/ the database type
     * @param entityType: The class name e.g. "MyApp.User" of the Entity
     * @param dbType: The type of the database to create
     * @param db: The implementation of IDb providing core database sql operations
     * @param tableName: Optional name of the table for the model if different than entity name
     * @param utc: Optional flag to save all datetimes in UTC ( defaults to false )
     * @param enc: Optional encrptor to support encryption of selected columns
     * @param mapper:  Mapper to conver to/from sql/records
     * @param namer: Optional namer to create naming conventions
     */
    fun <TId, T> repo(
            dbType: DbType,
            db:IDb,
            entityType: KClass<*>,
            entityIdType:KClass<*>,
            mapper: OrmMapper<TId, T>,
            tableName: String,
            utc: Boolean = false,
            enc: Encryptor? = null,
            namer: Namer? = null
    ): EntityRepo<TId, T> where TId:Comparable<TId>, T : Entity<TId> {

        // Repo: Handles all the CRUD / lookup functionality
        return when (dbType) {
            DbTypeMySql -> MySqlEntityRepo(db, entityType, entityIdType, mapper, tableName, enc, namer)
            DbTypePGres -> PostGresEntityRepo(db, entityType, entityIdType, mapper, tableName, enc, namer)
            else -> EntityRepoInMemory(entityType, entityIdType, mapper.model(), enc, namer)
        }
    }


    /**
     * Builds the repository associated w/ the database type
     * @param dbType: The type of the database to create
     * @param entityType   :  Type of the Entity / Domain class ( e.g. User )
     * @param entityIdType :  Type of the id of the Entity / Domain class ( e.g. Long )
     * @param tableName    :  Optional name of the database table for entityt ( defaults to class name e.g. "user" )
     * @param mapper       :  Mapper to conver to/from sql/records
     * @param namer: Optional namer to create naming conventions
     */
    fun <TId, T> repo(dbType: DbType, entityIdType: KClass<*>, entityType: KClass<*>,
                      tableName:String?, mapper: OrmMapper<TId, T>, namer: Namer? = null): EntityRepo<TId, T>
            where TId:Comparable<TId>, T: Entity<TId> {
        // 1. Table name
        val table = buildTableName(entityType, tableName, namer)

        // 3. Connection info ( using default connection )
        val con = this.con()

        // 4. Db ( JDBC database call wrapper with connection )
        val db = this.db( con )

        // 6. Repo ( provides CRUD using the Mapper)
        val repo = this.repo(dbType, db, entityType, entityIdType, mapper, table)

        return repo
    }
}

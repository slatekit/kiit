package slatekit.orm

import slatekit.common.db.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.db.DbType.*
import slatekit.common.naming.Namer
import slatekit.common.db.DbType
import slatekit.entities.Entity
import slatekit.entities.core.EntityBuilder
import slatekit.entities.Repo
import slatekit.entities.core.EntityInfo
import slatekit.entities.core.buildTableName
import slatekit.entities.repos.*
import slatekit.meta.KTypes
import slatekit.orm.core.SqlBuilder
import slatekit.orm.databases.vendors.*
import slatekit.meta.models.Model

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
     * @param info: Info about entity
     */
    fun <TId, T> mapper(dbType: DbType, db:IDb, model:Model, info:EntityInfo)
            : OrmMapper<TId, T> where TId:Comparable<TId>, T: Entity<TId> {
        return when (dbType) {
            DbTypeMySql -> MySqlMapper(model, db, info)
            DbTypePGres -> PostGresMapper(model, db, info)
            else        -> OrmMapper(model, db, MySqlConverter(), info)
        }
    }


    /**
     * Builds the mapper by loading up the connection info for the database key / shard provided.
     * @param dbType: The type of the database to create
     * @param info: Info about entity
     */
    fun <TId, T> mapper(dbType: DbType, info:EntityInfo): OrmMapper<TId, T>
            where TId:Comparable<TId>, T: Entity<TId> {
        // 1. Table name
        val table = buildTableName(info.entityType, info.tableName, info.namer)

        // 2. Model ( schema of the entity which maps fields to columns and has other metadata )
        val model = this.model(info.entityType, info.namer, table)

        // 3. Connection info ( using default connection )
        val con = this.con()

        // 4. Db ( JDBC database call wrapper with connection )
        val db = this.db( con )

        // 5. Mapper ( maps entities to/from sql using the model/schema )
        val mapper = this.mapper<TId, T>(dbType, db, model, info)

        return mapper
    }


    /**
     * Builds the repository associated w/ the database type
     * @param dbType: The type of the database to create
     * @param db: The implementation of IDb providing core database sql operations
     * @param info:  Info about entity
     * @param mapper:  Mapper to conver to/from sql/records
     */
    fun <TId, T> repo(
            dbType: DbType,
            db:IDb,
            info:EntityInfo,
            mapper: OrmMapper<TId, T>
    ): Repo<TId, T> where TId:Comparable<TId>, T : Entity<TId> {

        // Repo: Handles all the CRUD / lookup functionality
        return when (dbType) {
            DbTypeMySql -> MySqlRepo(db, info, mapper)
            DbTypePGres -> PostGresRepo(db, info, mapper)
            else -> {
                val result = when(info.entityIdType){
                    KTypes.KIntClass  -> InMemoryRepo<TId, T>(info, IntIdGenerator() as IdGenerator<TId>)
                    KTypes.KLongClass -> InMemoryRepo<TId, T>(info, LongIdGenerator() as IdGenerator<TId>)
                    else -> throw Exception("Unexpected entity id type for Slate Kit repo")
                }
                return result
            }
        }
    }


    /**
     * Builds the repository associated w/ the database type
     * @param dbType: The type of the database to create
     * @param mapper       :  Mapper to conver to/from sql/records
     */
    fun <TId, T> repo(dbType: DbType, info:EntityInfo, mapper: OrmMapper<TId, T>): Repo<TId, T>
            where TId:Comparable<TId>, T: Entity<TId> {

        // 1. Connection info ( using default connection )
        val con = this.con()

        // 2. Db ( JDBC database call wrapper with connection )
        val db = this.db( con )

        // 3. Repo ( provides CRUD using the Mapper)
        val repo = this.repo(dbType, db, info, mapper)
        return repo
    }
}

package slatekit.orm.databases.vendors

import slatekit.db.Db
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.query.Query
import slatekit.orm.core.Entity
import slatekit.orm.core.EntityMapper
import slatekit.orm.databases.Converter
import slatekit.orm.databases.SqlBuilder
import slatekit.orm.databases.TypeMap
import slatekit.orm.repos.EntityRepoSql
import slatekit.meta.models.Model
import kotlin.reflect.KClass

/**
 * MySql to Java types
 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
 */
object PostGreseMap : TypeMap()


/**
 * Contains all the converters for each type
 * Only customizations form the common one go here
 */
object PostGresConverter : Converter()


class PostGresBuilder(namer: Namer?) : SqlBuilder(PostGreseMap, namer)


class PostGresQuery : Query()




/**
 * Repository class specifically for MySql
 * @param entityType : The data type of the entity/model
 * @param entityIdType : The data type of the primary key/identity field
 * @param entityMapper : The entity mapper that maps to/from entities / records
 * @param nameOfTable : The name of the table ( defaults to entity name )
 * @param db
 * @tparam T
 */
open class PostGresEntityRepo<TId, T>(
        db: Db,
        entityType: KClass<*>,
        entityIdType: KClass<*>? = null,
        entityMapper: EntityMapper? = null,
        nameOfTable: String? = null,
        encryptor: Encryptor? = null,
        namer: Namer? = null
) : EntityRepoSql<TId, T>(
        db = db,
        entityType = entityType,
        entityIdType = entityIdType,
        entityMapper = entityMapper,
        nameOfTable = nameOfTable,
        encryptor = encryptor,
        namer = namer,
        encodedChar = '"',
        query = { PostGresQuery() },
        lastId = null
) where TId:Comparable<TId>, T:Entity<TId>


/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
open class PostGresEntityMapper(model: Model,
                                utc: Boolean = false,
                                enc: Encryptor? = null,
                                namer: Namer? = null)
    : EntityMapper(model, PostGresConverter, utc, '"', enc, namer)

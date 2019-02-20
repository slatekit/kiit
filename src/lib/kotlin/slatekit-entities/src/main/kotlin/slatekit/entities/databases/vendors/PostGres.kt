package slatekit.entities.databases.vendors

import slatekit.common.db.Db
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.query.Query
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityMapper
import slatekit.entities.databases.Converter
import slatekit.entities.databases.SqlBuilder
import slatekit.entities.databases.TypeMap
import slatekit.entities.repos.EntityRepoSql
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
open class PostGresEntityRepo<T>(
        db: Db,
        entityType: KClass<*>,
        entityIdType: KClass<*>? = null,
        entityMapper: EntityMapper? = null,
        nameOfTable: String? = null,
        encryptor: Encryptor? = null,
        namer: Namer? = null
) : EntityRepoSql<T>(
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
) where T : Entity


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

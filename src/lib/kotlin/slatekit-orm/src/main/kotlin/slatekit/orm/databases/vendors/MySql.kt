package slatekit.orm.databases.vendors

import slatekit.db.Db
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.entities.repos.EntityRepoSql
import slatekit.query.Query
import slatekit.entities.core.Entity
import slatekit.orm.core.EntityMapper
import slatekit.orm.databases.Converter
import slatekit.orm.databases.SqlBuilder
import slatekit.orm.databases.TypeMap
import slatekit.meta.models.Model
import slatekit.orm.core.OrmMapper
import kotlin.reflect.KClass

/**
 * MySql to Java types
 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
 */
object MySqlTypeMap : TypeMap()


/**
 * Contains all the converters for each type
 * Only customizations form the common one go here
 */
object MySqlConverter : Converter()


class MySqlBuilder(namer: Namer?) : SqlBuilder(MySqlTypeMap, namer)


class MySqlQuery : Query()


/**
 * Repository class specifically for MySql
 * @param entityType : The data type of the entity/model
 * @param entityIdType : The data type of the primary key/identity field
 * @param entityMapper : The entity mapper that maps to/from entities / records
 * @param nameOfTable : The name of the table ( defaults to entity name )
 * @param db
 * @tparam T
 */
open class MySqlEntityRepo<TId, T>(
        db: Db,
        entityType: KClass<*>,
        entityIdType: KClass<*>,
        entityMapper: OrmMapper<TId, T>,
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
        encodedChar = '`',
        query = { MySqlQuery() },
        lastId = "SELECT LAST_INSERT_ID();"
) where TId:Comparable<TId>, T: Entity<TId>



/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
open class MySqlEntityMapper(model: Model,
                             utc: Boolean = false,
                             enc: Encryptor? = null,
                             namer: Namer? = null)
    : EntityMapper(model, MySqlConverter, utc, '`', enc, namer)

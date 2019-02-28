package slatekit.orm.databases.vendors

import slatekit.common.db.IDb
import slatekit.db.Db
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.entities.repos.EntityRepoSql
import slatekit.query.Query
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityMapper
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
class MySqlConverter<TId, T> : Converter<TId, T>() where TId:kotlin.Comparable<TId>, T:Entity<TId>


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
        db: IDb,
        entityType: KClass<*>,
        entityIdType: KClass<*>,
        entityMapper: EntityMapper<TId, T>,
        nameOfTable: String,
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
        query = { MySqlQuery() }
) where TId:Comparable<TId>, T: Entity<TId>



/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
open class MySqlEntityMapper<TId, T>(model: Model,
                             utc: Boolean = false,
                             enc: Encryptor? = null,
                             namer: Namer? = null)
    : OrmMapper<TId, T>(model, MySqlConverter(), utc, '`', enc, namer)
        where TId:Comparable<TId>, T: Entity<TId>

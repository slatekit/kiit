package slatekit.orm.databases.vendors

import slatekit.common.db.IDb
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.entities.repos.EntityRepoSql
import slatekit.query.Query
import slatekit.entities.Entity
import slatekit.entities.core.buildTableName
import slatekit.orm.core.Converter
import slatekit.orm.core.SqlBuilder
import slatekit.orm.core.TypeMap
import slatekit.meta.models.Model
import slatekit.orm.OrmMapper
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
class MySqlConverter<TId, T> : Converter<TId, T>() where TId : kotlin.Comparable<TId>, T : Entity<TId>


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
        entityMapper: OrmMapper<TId, T>,
        nameOfTable: String? = null,
        encryptor: Encryptor? = null,
        namer: Namer? = null
) : EntityRepoSql<TId, T>(
        db = db,
        entityType = entityType,
        entityIdType = entityIdType,
        entityMapper = entityMapper,
        nameOfTable = buildTableName(entityType, nameOfTable, namer),
        encryptor = encryptor,
        namer = namer,
        encodedChar = '`',
        query = { MySqlQuery() }
) where TId : Comparable<TId>, T : Entity<TId> {

    private val ormMapper = entityMapper

    override fun create(entity: T): TId {
        return ormMapper.insert(entity)
    }


    override fun update(entity: T): Boolean {
        return ormMapper.update(entity)
    }
}


/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
open class MySqlEntityMapper<TId, T>(
        model: Model,
        db:IDb,
        idType:KClass<*>,
        utc: Boolean = false,
        enc: Encryptor? = null,
        namer: Namer? = null)
    : OrmMapper<TId, T>(model, db, idType, MySqlConverter(), utc, '`', enc, namer)
        where TId : Comparable<TId>, T : Entity<TId>

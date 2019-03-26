package slatekit.orm.databases.vendors

import slatekit.common.db.IDb
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.query.Query
import slatekit.meta.models.Model
import slatekit.entities.Entity
import slatekit.entities.core.buildTableName
import slatekit.entities.repos.EntityRepoSql
import slatekit.orm.core.Converter
import slatekit.orm.core.SqlBuilder
import slatekit.orm.core.TypeMap
import slatekit.orm.OrmMapper
import kotlin.reflect.KClass

/**
 * MySql to Java types
 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
 */
object PostGresMap : TypeMap()


/**
 * Contains all the converters for each type
 * Only customizations form the common one go here
 */
class PostGresConverter<TId, T> : Converter<TId, T>() where TId : kotlin.Comparable<TId>, T : Entity<TId>


class PostGresBuilder(namer: Namer?) : SqlBuilder(PostGresMap, namer)


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
        encodedChar = '"',
        query = { PostGresQuery() }
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
open class PostGresEntityMapper<TId, T>(
        model: Model,
        db:IDb,
        idType:KClass<*>,
        utc: Boolean = false,
        enc: Encryptor? = null,
        namer: Namer? = null)
    : OrmMapper<TId, T>(model, db, idType, MySqlConverter(), utc, '"', enc, namer)
        where TId : kotlin.Comparable<TId>, T : Entity<TId>

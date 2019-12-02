/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.orm

import slatekit.common.db.Vendor
import slatekit.db.builders.DbBuilder
import slatekit.db.builders.MySqlBuilder
import slatekit.db.builders.PostGresBuilder
import slatekit.entities.Entities
import slatekit.entities.Entity
import slatekit.entities.EntityService
import slatekit.entities.core.*
import slatekit.meta.models.Model
import slatekit.orm.core.SqlBuilder
import slatekit.orm.databases.vendors.MySqlTypeMap
import slatekit.orm.databases.vendors.PostGresMap
import kotlin.reflect.KClass




/**
 * Register the entity using just the id type and entity type.
 * This basically allows for the least information needed to
 * register a Entity
 */
fun <TId, T> Entities.model(
        vendor:Vendor,
        entityIdType: KClass<*>,
        entityType: KClass<*>,
        tableName: String? = null,
        persistUTC: Boolean = false): EntityContext where TId : Comparable<TId>, T : Entity<TId> {

    return orm<TId, T>(vendor, entityType, entityIdType, tableName,
            null, null, persistUTC)
}


/**
 * @param vendor       :  Database type see[Vendor]
 * @param entityType   :  Type of the Entity / Domain class ( e.g. User )
 * @param entityIdType :  Type of the id of the Entity / Domain class ( e.g. Long )
 * @param tableName    :  Optional name of the database table for entityt ( defaults to class name e.g. "user" )
 * @param serviceType  :  Optional type of the [EntityService] to create instance
 * @param serviceCtx   :  Context info to pass to the [EntityService] type for creation
 * @param persistUTC   :
 */
fun <TId, T> Entities.orm(
        vendor:Vendor,
        entityIdType: KClass<*>,
        entityType: KClass<*>,
        tableName: String? = null,
        serviceType: KClass<*>? = null,
        serviceCtx: Any? = null,
        persistUTC: Boolean = false):EntityContext where TId : Comparable<TId>, T : Entity<TId> {

    // NOTE: The ORM builder is an "enhanced" Builder of Entity/ORM components
    // compared to the EntityBuilder. This is because the EntityBuilder does
    // NOT do any auto-mapping, and does NOT know of any specific database.
    // So you have to explicitly supply your Repo/Mapper. However, the
    // ORM builder can build the appropriate Repo/Mapper/Converters
    val builder = OrmBuilder(this.builder.dbCreator, this.dbs, this.enc)

    // 1. Table name
    val table = buildTableName(entityType, tableName, namer)

    // 2. Model ( schema of the entity which maps fields to columns and has other metadata )
    val model = builder.model(entityType, namer, table)

    // 3. Connection info ( using default connection )
    val con = builder.con()

    // 4. Db ( JDBC database call wrapper with connection )
    val db = builder.db( con )

    // 5. Mapper ( maps entities to/from sql using the model/schema )
    val info = EntityInfo(entityIdType, entityType, table, '`', model, this.enc, this.namer)
    val mapper = builder.mapper<TId, T>(vendor, db, model, info)

    // 6. Repo ( provides CRUD using the Mapper)
    val repo = builder.repo(vendor, db, info, mapper)

    // 7. Service ( used to provide validation, placeholder for business functionality )
    val service = builder.service(this, serviceType, repo, serviceCtx)

    // 8. Entity context captures all relevant info about a mapped Entity( id type, entity type, etc. )
    val context = EntityContext(entityType, entityIdType, service::class, repo, mapper, vendor, model, "", "",
            service, serviceCtx)

    // 9. Finally Register
    this.register(context)

    return context
}


/**
 * Gets the Database source to Build DDL
 */
fun Entities.getDbSource(dbKey: String = "", dbShard: String = ""): DbBuilder {
    val dbType = builder.con(dbKey, dbShard)

    // Only supporting MySql for now.
    val source = dbType?.let { type ->
        when (type.driver) {
            Vendor.MySql.driver -> MySqlBuilder()
            Vendor.PGres.driver -> PostGresBuilder()
            else -> MySqlBuilder()
        }
    }
    return source
}


/**
 * Gets the Database source to Build DDL
 */
fun Entities.sqlBuilder(entityFullName:String): SqlBuilder {
    val ctx = getInfoByName(entityFullName)
    // Only supporting MySql for now.
    val sqlBuilder = when (ctx.vendor) {
        Vendor.MySql -> SqlBuilder(MySqlTypeMap, namer)
        Vendor.PGres -> SqlBuilder(PostGresMap, namer)
        else                      -> SqlBuilder(MySqlTypeMap, namer)
    }
    return sqlBuilder
}


/**
 * Gets a registered model ( schema for an entity ) for the entity type
 */
fun Entities.getModel(entityType: KClass<*>): Model? {
    return getModel(entityType.qualifiedName ?: "")
}



/**
 * Gets a registered model ( schema for an entity ) for the entity type
 */
fun Entities.getModel(fullName:String): Model? {
    val entityKey = builder.key(fullName)
    val entityCtx = this.getInfoByKey(entityKey)
    return when(entityCtx) {
        null -> {
            logger.error("Model not found for $entityKey")
            throw IllegalArgumentException("model not found for: $fullName")
        }
        else -> entityCtx.model
    }
}

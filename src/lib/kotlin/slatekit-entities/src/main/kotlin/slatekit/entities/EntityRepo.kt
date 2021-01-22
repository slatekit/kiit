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

package slatekit.entities

import kotlin.reflect.KProperty
import slatekit.common.DateTime
import slatekit.common.data.IDb
import slatekit.common.data.Mapper
import slatekit.data.CrudRepo
import slatekit.data.Repo
import slatekit.data.features.Deletable
import slatekit.data.features.Findable
import slatekit.entities.Consts.idCol
import slatekit.entities.EntityWithTime
import slatekit.entities.core.EntityInfo
import slatekit.entities.core.EntityStore
import slatekit.entities.repos.SqlRepo
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query
import slatekit.query.where
import kotlin.reflect.KClass

/**
 * Base Entity repository using generics with support for all the CRUD methods.
 * NOTE: This is basically a GenericRepository implementation
 * @tparam T
 */
interface EntityRepo<TId, T> : EntityStore,
    CrudRepo<TId, T>,
    Findable<TId, T>,
    Deletable<TId, T>
    where TId : Comparable<TId> {

    val info: EntityInfo

    /**
     * the name of the id field.
     * @return
     */
    override fun id(): String = info.model?.idField?.name ?: idCol

    /**
     * The name of the table in the datastore
     */
    override fun name(): String = info.name()

    /**
     * updates items using the query
     */
    fun updateByQuery(query: IQuery): Int

    /**
     * deletes items using the query
     */
    fun deleteByQuery(query: IQuery): Int

    /**
     * finds items based on the query
     */
    fun findByQuery(query: IQuery): List<T> = listOf()

    /**
     * Gets the total number of records based on the query provided.
     */
    fun countByQuery(query: IQuery): Long

    /**
     * Return a query builder for more complex searches
     */
    fun query(): Query = info.queryBuilder?.invoke() ?: Query()

    /**
     * Gets the column name for the Kproperty from the model schema if available
     * or defaults to the property name.
     */
    fun columnName(prop: KProperty<*>): String {
        return columnName(prop.name)
    }

    /**
     * Gets the column name for the Kproperty from the model schema if available
     * or defaults to the property name.
     */
    fun columnName(name:String): String {
        val model = info.model
        return when (model) {
            null -> name
            else -> if (model.any) model.fields.first { it.name == name }.storedName else name
        }
    }


    companion object {

        inline fun <reified TId, reified T> h2(db: IDb, mapper: Mapper<TId, T>, table: String? = null): SqlRepo<TId, T> where TId : Comparable<TId>, T: Any {
            return sqlRepo<TId, T>(db, mapper, table) { idType, enType, info ->
                SqlRepo(db, info, mapper)
            }
        }


        inline fun <reified TId, reified T> mysql(db: IDb, mapper: Mapper<TId, T>, table: String? = null): SqlRepo<TId, T> where TId : Comparable<TId>, T: Any {
            return sqlRepo<TId, T>(db, mapper, table) { idType, enType, info ->
                SqlRepo(db, info, mapper)
            }
        }

        inline fun <reified TId, reified T> postgres(db: IDb, mapper: Mapper<TId, T>, table: String? = null): SqlRepo<TId, T> where TId : Comparable<TId>, T: Any {
            return sqlRepo<TId, T>(db, mapper, table) { idType, enType, info ->
                SqlRepo(db, info, mapper)
            }
        }

        inline fun <reified TId, reified T> sqlRepo(db: IDb,
                                                    mapper: Mapper<TId, T>,
                                                    table: String? = null,
                                                    op: (KClass<TId>, KClass<T>, EntityInfo) -> SqlRepo<TId, T>): SqlRepo<TId, T>
            where TId : Comparable<TId>, T: Any {
            val idType = TId::class
            val enType = T::class
            val tableName = table ?: enType.simpleName!!.toLowerCase()
            val info = EntityInfo(idType, enType, tableName)
            val repo = op(idType, enType, info)
            return repo
        }

    }
}

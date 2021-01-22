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
import slatekit.common.data.IDb
import slatekit.data.CrudRepo
import slatekit.data.core.Meta
import slatekit.data.features.Deletable
import slatekit.data.features.Findable
import slatekit.data.features.Patchable
import slatekit.data.statements.SqlStatements
import slatekit.entities.core.EntityInfo
import slatekit.query.IQuery
import slatekit.query.Query
import kotlin.reflect.KClass

/**
 * Base Entity repository using generics with support for all the CRUD methods.
 * NOTE: This is basically a GenericRepository implementation
 * @tparam T
 */
interface EntityRepo<TId, T> :
    CrudRepo<TId, T>,
    Findable<TId, T>,
    Deletable<TId, T>,
    Patchable<TId, T>
    where TId : Comparable<TId>, T : Any {

    val info: EntityInfo

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


    fun findOneByQuery(query: IQuery): T?

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
    fun columnName(name: String): String {
        val model = info.model
        return when (model) {
            null -> name
            else -> if (model.any) model.fields.first { it.name == name }.storedName else name
        }
    }


    companion object {

        inline fun <reified TId, reified T> h2(db: IDb, meta: Meta<TId, T>, mapper: EntityMapper<TId, T>): EntitySqlRepo<TId, T> where TId : Comparable<TId>, T : Any {
            return sqlRepo<TId, T>(db, mapper, meta.table.name) { idType, enType, info ->
                val stmts = SqlStatements(meta, mapper)
                EntitySqlRepo(db, info, meta, stmts, mapper)
            }
        }


        inline fun <reified TId, reified T> mysql(db: IDb, meta: Meta<TId, T>, mapper: EntityMapper<TId, T>, table: String? = null): EntitySqlRepo<TId, T> where TId : Comparable<TId>, T : Any {
            return sqlRepo<TId, T>(db, mapper, meta.table.name) { idType, enType, info ->
                val stmts = SqlStatements(meta, mapper)
                EntitySqlRepo(db, info, meta, stmts, mapper)
            }
        }


        inline fun <reified TId, reified T> postgres(db: IDb, meta: Meta<TId, T>, mapper: EntityMapper<TId, T>, table: String? = null): EntitySqlRepo<TId, T> where TId : Comparable<TId>, T : Any {
            return sqlRepo<TId, T>(db, mapper, meta.table.name) { idType, enType, info ->
                val stmts = SqlStatements(meta, mapper)
                EntitySqlRepo(db, info, meta, stmts, mapper)
            }
        }


        inline fun <reified TId, reified T> sqlRepo(db: IDb,
                                                    mapper: EntityMapper<TId, T>,
                                                    table: String? = null,
                                                    op: (KClass<TId>, KClass<T>, EntityInfo) -> EntitySqlRepo<TId, T>): EntitySqlRepo<TId, T>
            where TId : Comparable<TId>, T : Any {
            val idType = TId::class
            val enType = T::class
            val tableName = table ?: enType.simpleName!!.toLowerCase()
            val info = EntityInfo(idType, enType, tableName)
            val repo = op(idType, enType, info)
            return repo
        }

    }
}

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

import slatekit.common.data.IDb
import slatekit.common.data.Mapper
import slatekit.common.ext.tail
import slatekit.data.core.Meta
import slatekit.data.features.Countable
import slatekit.data.features.Orderable
import slatekit.entities.core.EntityInfo
import slatekit.data.syntax.Syntax
import slatekit.query.IQuery
import slatekit.common.data.Compare
import slatekit.data.SqlRepo

/**
 * This Simply extends the @see[slatekit.data.SqlRepo] with additional functionality
 * 1. querying using the @see[slatekit.query.Query] component
 * @param db : Db wrapper to execute sql
 * @param info : Holds all info relevant state/members needed to perform repo operations
 * @tparam T
 */
open class EntitySqlRepo<TId, T>(
    db: IDb,
    override val info: EntityInfo,
    meta: Meta<TId, T>,
    mapper: Mapper<TId, T>,
    syntax: Syntax<TId, T>
) : SqlRepo<TId, T>(db, meta, syntax, mapper), EntityRepo<TId, T>, Countable<TId, T>, Orderable<TId, T> where TId : Comparable<TId>, T : Any {

    /** ====================================================================================
     * Methods using @see[slatekit.query.Query]
     * ====================================================================================
     */

    /**
     * updates items using the query
     * @param query: The query builder
     */
    override fun updateByQuery(query: IQuery): Int {
        val prefix = syntax.update.prefix()
        val updateSql = query.toUpdatesText()
        val sql = "$prefix $updateSql;"
        return update(sql)
    }

    /**
     * deletes items using the query
     * @param query: The query builder
     * @return
     */
    override fun deleteByQuery(query: IQuery): Int {
        val prefix = syntax.delete.prefix()
        val filter = query.toFilter()
        val sql = "$prefix where $filter;"
        return update(sql)
    }

    /**
     * Gets the total number of records based on the query provided.
     */
    override fun countByQuery(query: IQuery): Long {
        val prefix = syntax.select.count()
        val filter = query.toFilter()
        val sql = "$prefix where $filter;"
        val count = getScalarLong(sql)
        return count
    }

    /**
     * Finds items using the query builder
     */
    override fun findByQuery(query: IQuery): List<T> {
        val prefix = syntax.select.prefix()
        val filter = query.toFilter()
        val sql = "$prefix where $filter;"
        val results = mapAll(sql)
        return results ?: listOf()
    }

    /**
     * finds first item based on the query
     * @param query: name of field
     * @return
     */
    override fun findOneByQuery(query: IQuery): T? {
        return findByQuery(query.limit(1)).firstOrNull()
    }
}

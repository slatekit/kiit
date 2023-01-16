/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.entities

import kiit.common.data.BuildMode
import kiit.common.data.DataHooks
import kiit.common.data.IDb
import kiit.data.Mapper
import kiit.data.SqlRepo
import kiit.data.core.Meta
import kiit.data.sql.Provider
import kiit.entities.mapper.EntityMapper

/**
 * Base Entity repository using generics with support for all the CRUD methods.
 * @tparam T
 */
open class EntityRepo<TId, T>(
    db: IDb,
    meta: Meta<TId, T>,
    mapper: Mapper<TId, T>,
    syntax: Provider<TId, T>,
    hooks: DataHooks<TId, T>? = null,
    mode: BuildMode = BuildMode.Prep)
    : SqlRepo<TId, T>(db, meta, mapper, syntax, hooks, mode) where TId : Comparable<TId>, T : Any {

    private val lookup:Map<String, String> = if(mapper is EntityMapper<*, *>) {
        mapper.model.fields.map { it.name to it.storedName }.toMap()
    }
    else {
        mapOf()
    }


    /**
     * Used when field name is different than table column name
     */
    override fun columnName(fieldName: String): String {
        val name = if(lookup.isEmpty()) super.columnName(fieldName) else lookup[fieldName] ?: fieldName
        return name
    }
}

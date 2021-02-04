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
import slatekit.data.SqlRepo
import slatekit.data.core.Meta
import slatekit.data.sql.Dialect
import slatekit.data.sql.Provider
import slatekit.entities.mapper.EntityMapper

/**
 * Base Entity repository using generics with support for all the CRUD methods.
 * @tparam T
 */
open class EntityRepo<TId, T>(
    db: IDb,
    meta: Meta<TId, T>,
    mapper: Mapper<TId, T>,
    syntax: Provider<TId, T>) : SqlRepo<TId, T>(db, meta, mapper, syntax) where TId : Comparable<TId>, T : Any {
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

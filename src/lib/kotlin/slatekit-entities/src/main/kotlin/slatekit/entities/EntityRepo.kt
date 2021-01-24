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
import slatekit.data.syntax.Syntax

/**
 * Base Entity repository using generics with support for all the CRUD methods.
 * NOTE: This is basically a GenericRepository implementation
 * @tparam T
 */
open class EntityRepo<TId, T>(
    db: IDb,
    meta: Meta<TId, T>,
    mapper: Mapper<TId, T>,
    syntax: Syntax<TId, T>) : SqlRepo<TId, T>(db, meta, mapper, syntax) where TId : Comparable<TId>, T : Any {
}

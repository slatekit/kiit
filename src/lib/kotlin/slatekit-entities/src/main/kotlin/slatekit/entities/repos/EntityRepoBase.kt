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

package slatekit.entities.repos

import slatekit.entities.Entity
import slatekit.entities.EntityRepo
import slatekit.entities.core.EntityInfo
import slatekit.entities.core.EntityStore

/**
 * Base Entity repository using generics with support for all the CRUD methods.
 * NOTE: This is basically a GenericRepository implementation
 * @param info   : Holds all info relevant state/members needed to perform repo operations
 * @tparam T
 */
abstract class EntityRepoBase<TId, T>(
        override val info: EntityInfo
)
    : EntityStore, EntityRepo<TId, T> where TId:Comparable<TId>, T : Entity<TId> {

    protected val name:String by lazy { info.tableName }


    /**
     * The name of the table in the datastore
     */
    override fun name(): String {
        return name
    }
}

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

import slatekit.entities.core.EntityStore

/**
 * Entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
open class EntityService<TId, T>(protected val repo: EntityRepo<TId, T>) : EntityServices<TId, T>
        where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    override fun store(): EntityStore = repo
    override fun repo(): EntityRepo<TId, T> = repo
}

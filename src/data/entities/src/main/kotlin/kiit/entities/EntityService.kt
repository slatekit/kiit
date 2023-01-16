/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.entities

import kiit.entities.features.CRUD
import kiit.entities.features.Counts
import kiit.entities.features.Ordered

/**
 * Entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
open class EntityService<TId, T>(protected val repo: EntityRepo<TId, T>)
    : CRUD<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {
    override fun repo(): EntityRepo<TId, T> = repo
}


/**
 * Contains all features beyond simple CRUD functions ( counts, ordered, etc )
 */
open class EntityServices<TId, T>(repo: EntityRepo<TId, T>)
: EntityService<TId, T>(repo), Counts<TId, T>, Ordered<TId, T> where TId : kotlin.Comparable<TId>, TId : Number, T : Entity<TId> {
}

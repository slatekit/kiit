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

import slatekit.entities.core.IEntityRepo
import slatekit.entities.services.EntityServices




/**
 * Base entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
open class EntityService<TId,T>(
        protected val entities: Entities,
        protected val repo: EntityRepo<TId, T>
)
    : EntityServices<TId, T> where TId: Comparable<TId>, T : Entity<TId> {

    override fun entities(): Entities = entities

    override fun repo(): IEntityRepo = repo

    override fun repoT(): EntityRepo<TId, T> = repo
}

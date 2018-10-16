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

package slatekit.entities.core

import slatekit.entities.services.EntityServices

/**
 * Base entity service with generics to support all CRUD operations.
 * Delegates calls to the entity repository, and also manages the timestamps
 * on the entities for create/update operations
 * @tparam T
 */
open class EntityService<T>(protected val _entities:Entities,
                            protected val _repo: EntityRepo<T>)
    : EntityServices<T> where T : Entity{


    override fun entityRepo(): EntityRepo<T> = _repo


    override fun entities(): Entities = _entities


    /**
     * gets the repo representing the underlying datastore
     * @return
     */
    override fun repo(): IEntityRepo = _repo
}


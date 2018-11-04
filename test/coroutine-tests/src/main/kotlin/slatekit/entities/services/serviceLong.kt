package slatekit.entities.services


/* coroutines
import slatekit.async.coroutines.AsyncContextCoroutine
import slatekit.async.coroutines.AsyncExtensions
import slatekit.async.coroutines.Future
import kotlinx.coroutines.*
// */

///* java futures
import slatekit.async.futures.AsyncContextFuture
import slatekit.async.futures.AsyncExtensions
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityServiceWithId
import slatekit.entities.repo.EntityRepo

// */

/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */


class EntityService<T>(
    repo: EntityRepo<T>,
    scope: AsyncContextFuture = AsyncContextFuture()
) : EntityServiceWithId<Long, T>(repo, scope), AsyncExtensions where T : Entity<Long>

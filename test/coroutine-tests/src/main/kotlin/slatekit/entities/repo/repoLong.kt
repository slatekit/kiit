package slatekit.entities.repo

//import slatekit.async.coroutines.Future
//import kotlinx.coroutines.*
import slatekit.async.futures.AsyncContextFuture
import slatekit.async.futures.AsyncExtensions
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityRepoWithId

//import java.util.concurrent.Future

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


class EntityRepo<TItem>(
    items: MutableList<TItem>,
    scope: AsyncContextFuture
) : EntityRepoWithId<Long, TItem>(items, scope),
    AsyncExtensions where TItem : Entity<Long> {

    private var lastId = items.maxBy { it.id }?.id ?: 0


    override fun nextId(): Long {
        lastId++
        return lastId
    }
}

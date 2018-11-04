package slatekit.entities.core


/* coroutines
import slatekit.async.coroutines.AsyncContextCoroutine
import slatekit.async.coroutines.AsyncExtensions
import slatekit.async.coroutines.Future
import kotlinx.coroutines.*
// */

///* java futures
import slatekit.async.futures.AsyncContextFuture
import slatekit.async.futures.AsyncExtensions
import slatekit.async.futures.Future
import slatekit.async.futures.await

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


open class EntityServiceWithId<TId, T>(
    val repo: EntityRepoWithId<TId, T>,
    override val scope: AsyncContextFuture = AsyncContextFuture()
) : AsyncExtensions where T : Entity<TId> {

    fun all(): Future<List<T>> {
        return repo.all()
    }


    fun get(id: TId): Future<T?> {
        return repo.get(id)
    }


    fun create(item: T): Future<TId> {
        return repo.create(item)
    }


    fun update(item: T): Future<Boolean> {
        return repo.update(item)
    }


    fun save(item: T): Future<Pair<Boolean, TId>> {
        return async {

            val t = get(item.id).await()

            when (t) {
                null -> Pair(true, create(item).await())
                else -> Pair(repo.update(item).await(), item.id)
            }
        }
    }

}

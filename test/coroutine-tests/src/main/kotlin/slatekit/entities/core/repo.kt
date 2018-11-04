package slatekit.entities.core

//import slatekit.async.coroutines.Future
//import kotlinx.coroutines.*
import slatekit.async.futures.AsyncContextFuture
import slatekit.async.futures.AsyncExtensions
import slatekit.async.futures.Future

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


abstract class EntityRepoWithId<TId, TItem>(
    val items: MutableList<TItem>,
    override val scope: AsyncContextFuture
) : AsyncExtensions where TItem : Entity<TId> {


    fun all(): Future<List<TItem>> {
        return async {
            items.toList()
        }
    }


    fun get(id: TId): Future<TItem?> {
        return async {
            items.firstOrNull { it.id == id }
        }
    }


    fun create(item: TItem): Future<TId> {
        return async {
            synchronized(this) {
                val id = nextId()
                val itemWithId = item.withId(id)
                items.add(itemWithId as TItem)
                id
            }
        }
    }


    fun update(item: TItem): Future<Boolean> {
        return async {
            synchronized(this) {
                val index = items.indexOfFirst { it.id == item.id }
                if (index < 0) {
                    false
                } else {
                    items[index] = item
                    true
                }
            }
        }
    }


    abstract fun nextId(): TId
}

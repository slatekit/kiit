package entities

import kotlinx.coroutines.*

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


class EntityRepo<T>(val items: MutableList<T>,
                    override val scope:CoroutineScope = GlobalScope) : AsyncExtensions where T : Entity {

    private var lastId = items.maxBy { it.id }?.id ?: 0


    fun all(): Future<List<T>> {
        return async {
            items.toList()
        }
    }


    fun get(id: Long): Future<T?> {
        return async {
            items.firstOrNull { it.id == id }
        }
    }


    fun create(item: T): Future<Long> {
        return async {
            synchronized(this) {
                val id = nextId()
                val itemWithId = item.withId(id)
                items.add(itemWithId as T)
                id
            }
        }
    }


    fun update(item: T): Future<Boolean> {
        return async {
            val index = items.indexOfFirst { it.id == item.id }
            if(index < 0 ) {
                false
            }
            else {
                items[index] = item
                true
            }
        }
    }


    private fun nextId(): Long {
        lastId++
        return lastId
    }
}

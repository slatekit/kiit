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


class EntityService<T>(
    val repo: EntityRepo<T>,
    override val scope: CoroutineScope = GlobalScope
) : AsyncExtensions where T : Entity {

    fun all(): Future<List<T>> {
        return repo.all()
    }


    fun get(id: Long): Future<T?> {
        return repo.get(id)
    }


    fun create(item: T): Future<Long> {
        return repo.create(item)
    }


    fun update(item: T): Future<Boolean> {
        return repo.update(item)
    }


    fun save(item: T): Future<Pair<Boolean, Long>> {
        return async {

            val t = get(item.id).await()

            when (t) {
                null -> Pair(true, create(item).await())
                else -> Pair(repo.update(item).await(), item.id)
            }
        }
    }

}

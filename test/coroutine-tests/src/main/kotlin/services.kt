import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

interface Entity {
    val id:Long

    fun withId(id:Long): Entity
}


class EntityRepo<T>(val items:MutableList<T>) where T: Entity  {

    private var lastId = items.maxBy { it.id }?.id ?: 0

    fun all():List<T> {
        return items.toList()
    }


    fun get(id:Long):T {
        return items.first { it.id == id }
    }


    fun create(item:T):Long {
        val id = nextId()
        val itemWithId = item.withId(id)
        items.add(itemWithId as T)
        return id
    }


    private fun nextId():Long {
        lastId++
        return lastId
    }
}


class EntityService<T>(val repo:EntityRepo<T>) where T: Entity  {

    fun all():List<T> {
        return repo.all()
    }

    fun get(id:Long):T {
        return repo.get(id)
    }


    fun create(item:T):Long {
        return repo.create(item)
    }
}


data class User(override val id:Long, val name:String):Entity {
    override fun withId(id: Long): Entity {
        return this.copy(id = id)
    }

}

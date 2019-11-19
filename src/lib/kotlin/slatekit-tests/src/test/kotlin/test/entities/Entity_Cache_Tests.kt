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

package test.entities

import org.junit.Assert
import org.junit.Test
import slatekit.entities.EntityService
import slatekit.entities.core.EntityInfo
import slatekit.entities.repos.InMemoryRepo
import slatekit.entities.repos.LongIdGenerator
import slatekit.entities.support.cache.EntityCache
import test.setup.User5


class Entity_Cache_Tests {


    fun cache(loadData:Boolean,
              immediateLoad:Boolean,
              fetcher:((EntityService<Long, User5>) -> List<User5>)? = null):EntityCache<Long, String, User5> {
        val info = EntityInfo( Long::class, User5::class, "")
        val repo = InMemoryRepo<Long, User5>(info, LongIdGenerator())

        if(loadData) {
            repo.create(User5(0, "user1@a.com", true , uniqueId = "a1"))
            repo.create(User5(0, "user2@a.com", true , uniqueId = "a2"))
            repo.create(User5(0, "user3@a.com", true , uniqueId = "a3"))
            repo.create(User5(0, "user4@a.com", false, uniqueId = "a4"))
            repo.create(User5(0, "user5@a.com", false, uniqueId = "a5"))
        }

        val service = EntityService<Long, User5>(repo)
        val cache = EntityCache<Long, String, User5>(
                service = service,
                keyLookup = { u -> u.uniqueId},
                fetcher = fetcher ?: { svc -> svc.getAll() },
                load = immediateLoad
        )

        return cache
    }


    @Test
    fun can_ensure_lazy() {
        val cache = cache(true, false)
        Assert.assertEquals(0, cache.size())
        Assert.assertEquals(true, cache.items().isEmpty())
    }


    @Test
    fun can_ensure_immediate_load() {
        val cache = cache(true, true)
        ensure(cache, 5, 4, 5, "a5", "user5@a.com", true)
    }


    @Test
    fun can_ensure_refresh() {
        val cache = cache(true, true)
        ensure(cache, 5, 5, 6, "a6", null, false)
        cache.settings.service.create(User5(0, "user6@a.com", true , uniqueId = "a6"))
        cache.refresh()
        ensure(cache, 6, 5, 6, "a6", "user6@a.com", true)
    }


    fun ensure(cache: EntityCache<Long, String, User5>, count:Int, ndx:Int, id:Long, key:String, email:String?, exists:Boolean){
        Assert.assertEquals(count, cache.size())
        Assert.assertEquals(exists, cache.contains(id))
        Assert.assertEquals(exists, cache.containsKey(key))
        Assert.assertEquals(exists, cache.get(ndx) != null)
        Assert.assertEquals(exists, cache.getByKey(key) != null)
        Assert.assertEquals(email, cache.get(ndx)?.email)
        Assert.assertEquals(email, cache.getById(id)?.email)
        Assert.assertEquals(email, cache.getByKey(key)?.email)
    }

}

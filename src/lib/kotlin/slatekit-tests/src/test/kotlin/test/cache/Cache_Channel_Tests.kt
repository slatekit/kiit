/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */
package test

import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test
import kiit.cache.*
import kiit.common.DateTime
import kiit.common.Identity
import kiit.common.log.LoggerConsole


class Cache_Channel_Tests {

    val CACHE_NAME = "unit-tests-cache"
    val id = Identity.app("kiit", "app", "cache")
    fun getCache(initialize: Boolean = true, settings: CacheSettings = CacheSettings(10), listener: ((CacheEvent) -> Unit)? = null): SimpleAsyncCache {
        val cache = SimpleAsyncCache.of(id, LoggerConsole(), settings, listener)
        return cache
    }

    fun runTest(initialize: Boolean = true, listener: ((CacheEvent) -> Unit)? = null, op: suspend (SimpleAsyncCache) -> Unit) {
        val scope = CoroutineScope(Dispatchers.IO)
        runBlocking {
            val cache = getCache(initialize = false, listener = listener)
            if (initialize) {
                cache.put("countries", "countries supported for mobile app", 60) { listOf("us", "ca") }
            }
            //cache.respond()
            scope.launch {
                cache.work()
            }
            op(cache)
            cache.stop()
        }
    }

    //@Test
    fun can_init() {
        runTest { cache ->
            val stats = cache.stats()
            Assert.assertEquals(1, stats.size)
        }
    }


    //@Test
    fun can_put() {
        val timestamp = DateTime.now()
        var event: CacheEvent? = null
        val listener = { ev: CacheEvent ->
            event = ev
        }
        runTest(listener = listener) { cache ->
            val countries1 = cache.get<List<String>>("countries")

            // Check values
            Assert.assertTrue(countries1!!.size == 2)
            Assert.assertTrue(countries1[0] == "us")
            Assert.assertTrue(countries1[1] == "ca")

            // Check stats
            val stats = cache.stats()
            Assert.assertEquals(1, stats.size)
            Assert.assertEquals(stats[0].key, "countries")

            // Errors
            Assert.assertNull(stats[0].error.value)
            Assert.assertNull(stats[0].error.created)
            Assert.assertNull(stats[0].error.updated)
            Assert.assertEquals(stats[0].error.count, 0)

            // Value
            Assert.assertNotNull(stats[0].value.value)
            Assert.assertNotNull(stats[0].value.created)
            Assert.assertNotNull(stats[0].value.updated)
            Assert.assertEquals(stats[0].value.count, 1)

            // Reads
            Assert.assertEquals(1, stats[0].hits.count)
            Assert.assertNotNull(stats[0].hits.updated)
            Assert.assertTrue(stats[0].hits.updated!! >= timestamp)

            // Expiry
            Assert.assertEquals(60, stats[0].expiry.seconds)
            Assert.assertTrue(stats[0].expiry.isAlive())
            Assert.assertTrue(timestamp <= stats[0].expiry.started)
            Assert.assertTrue(stats[0].expiry.expires > stats[0].expiry.started)

            // Events
            Assert.assertNotNull(event)
            Assert.assertEquals(CacheAction.Create, event?.action)
            Assert.assertEquals("countries", event?.key)
            Assert.assertTrue(!event?.uuid.isNullOrEmpty())
            Assert.assertEquals("kiit.app.cache.app.${CacheAction.Create.name}.countries", event?.name ?: "")
        }
    }


    //@Test
    fun can_get() {
        // Get 1
        val timestamp1 = DateTime.now()
        runTest { cache ->
            val countries1 = cache.get<List<String>>("countries")
            // Check values
            Assert.assertTrue(countries1!!.size == 2)
            Assert.assertTrue(countries1[0] == "us")
            Assert.assertTrue(countries1[1] == "ca")

            // Reads
            val stats1 = cache.stats()
            Assert.assertEquals(1, stats1[0].hits.count)
            Assert.assertNotNull(stats1[0].hits.updated)
            Assert.assertTrue(stats1[0].hits.updated!! >= timestamp1)

            // Get 2
            val timestamp2 = DateTime.now()
            val countries2Deferred = cache.getAsync<List<String>>("countries")
            val countries2 = runBlocking {
                cache.poll()
                countries2Deferred.await()
            }

            // Check values
            Assert.assertTrue(countries2!!.size == 2)
            Assert.assertTrue(countries2[0] == "us")
            Assert.assertTrue(countries2[1] == "ca")

            // Reads
            val stats2 = cache.stats()
            Assert.assertEquals(2, stats2[0].hits.count)
            Assert.assertNotNull(stats2[0].hits.updated)
            Assert.assertTrue(stats2[0].hits.updated!! >= timestamp1)
        }
    }


    //@Test
    fun can_check() {
        runTest { cache ->
            Assert.assertTrue(cache.contains("countries"))
            Assert.assertFalse(cache.contains("promocodes"))
        }
    }


    //@Test
    fun can_key() {
        runTest { cache ->
            cache.put("promocode", "promotion code", 60) { "promo-123" }
            cache.poll()

            // Keys
            val keys = cache.keys()
            Assert.assertTrue(keys.contains("countries"))
            Assert.assertTrue(keys.contains("promocode"))

            // Size
            val size = cache.size()
            Assert.assertEquals(2, size)
        }
    }


    //@Test
    fun can_clear() {
        val timestamp = DateTime.now()
        var event: CacheEvent? = null
        val listener = { ev: CacheEvent -> event = ev }
        runTest(listener = listener) { cache ->
            cache.put("promocode", "promotion code", 60) { "promo-123" }
            cache.poll()
            Assert.assertEquals(2, cache.keys().size)
            Assert.assertEquals(2, cache.size())

            cache.deleteAll()
            cache.poll()


            Assert.assertEquals(0, cache.keys().size)
            Assert.assertEquals(0, cache.size())

            // Events
            Assert.assertNotNull(event)
            Assert.assertEquals(CacheAction.DeleteAll, event?.action)
            Assert.assertEquals("*", event?.key)
            Assert.assertTrue(!event?.uuid.isNullOrEmpty())
            Assert.assertEquals("kiit.app.cache.app.${CacheAction.DeleteAll.name}.*", event?.name ?: "")
        }
    }


    //@Test
    fun can_set() {
        val timestamp = DateTime.now()
        var event: CacheEvent? = null
        val listener = { ev: CacheEvent -> event = ev }

        runTest(listener = listener) {cache ->
            val countries1 = cache.get<List<String>>("countries")

            // Check values
            Assert.assertTrue(countries1!!.size == 2)
            Assert.assertTrue(countries1[0] == "us")
            Assert.assertTrue(countries1[1] == "ca")

            // Check stats
            val stats1 = cache.stats()
            Assert.assertEquals(1, stats1.size)
            Assert.assertEquals(stats1[0].key, "countries")

            // Value
            Assert.assertNotNull(stats1[0].value.value)
            Assert.assertNotNull(stats1[0].value.created)
            Assert.assertNotNull(stats1[0].value.updated)
            Assert.assertEquals(stats1[0].value.count, 1)

            cache.set("countries", listOf("us", "ca", "uk"))
            cache.poll()
            val countries2Deferred = cache.getAsync<List<String>>("countries")
            val countries2 = runBlocking {
                cache.poll()
                countries2Deferred.await()
            }

            // Check values
            Assert.assertTrue(countries2!!.size == 3)
            Assert.assertTrue(countries2[0] == "us")
            Assert.assertTrue(countries2[1] == "ca")
            Assert.assertTrue(countries2[2] == "uk")

            // Check stats
            val stats2 = cache.stats()
            Assert.assertEquals(1, stats2.size)
            Assert.assertEquals(stats2[0].key, "countries")

            // Value
            Assert.assertNotNull(stats2[0].value.value)
            Assert.assertNotNull(stats2[0].value.created)
            Assert.assertNotNull(stats2[0].value.updated)
            Assert.assertEquals(stats2[0].value.count, 2)
        }
    }


    //@Test
    fun can_refresh() {
        val timestamp = DateTime.now()
        var event: CacheEvent? = null
        val listener = { ev: CacheEvent -> event = ev }

        runTest(listener = listener) {cache ->
            var count = 0
            cache.put("countries", "countries supported for mobile app", 300) {
                if (count == 0) {
                    count++
                    listOf("us", "ca")
                } else {
                    listOf("us", "ca", "uk")
                }
            }
            cache.poll()
            val countries1Deferred = cache.getAsync<List<String>>("countries")
            val countries1 = runBlocking {
                cache.poll()
                countries1Deferred.await()
            }

            // Check values
            Assert.assertTrue(countries1!!.size == 2)
            Assert.assertTrue(countries1[0] == "us")
            Assert.assertTrue(countries1[1] == "ca")

            // Check values after update
            val countries2Deferred = cache.getFreshAsync<List<String>>("countries")
            val countries2 = runBlocking {
                cache.poll()
                countries2Deferred.await()
            }

            Assert.assertTrue(countries2!!.size == 3)
            Assert.assertTrue(countries2[0] == "us")
            Assert.assertTrue(countries2[1] == "ca")
            Assert.assertTrue(countries2[2] == "uk")
        }
    }


    //@Test
    fun can_expire() {
        val timestamp1 = DateTime.now()
        var event: CacheEvent? = null
        val listener = { ev: CacheEvent -> event = ev }

        runTest(listener = listener, initialize = false) {cache ->
            var count = 0
            cache.put("countries", "countries supported for mobile app", 300) {
                if (count == 0) {
                    count++
                    listOf("us", "ca")
                } else {
                    listOf("us", "ca", "uk")
                }
            }
            cache.poll()
            cache.put("promocode", "promotion code", 300) { "promo-123" }
            cache.poll()

            // Get 1
            val countries1 = cache.get<List<String>>("countries")

            // Check values
            Assert.assertTrue(countries1!!.size == 2)
            Assert.assertTrue(countries1[0] == "us")
            Assert.assertTrue(countries1[1] == "ca")

            // Reads
            val stats1 = cache.stats().first { it.key == "countries" }
            Assert.assertEquals(1, stats1.hits.count)
            Assert.assertNotNull(stats1.hits.updated)
            Assert.assertTrue(stats1.hits.updated!! >= timestamp1)

            cache.expire("countries")
            cache.poll()

            // Get 2
            val countries2 = cache.getOrLoad<List<String>>("countries")

            val stats2 = cache.stats().first { it.key == "countries" }

            // Check values
            Assert.assertTrue(countries2!!.size == 3)
            Assert.assertTrue(countries2[0] == "us")
            Assert.assertTrue(countries2[1] == "ca")
            Assert.assertTrue(countries2[2] == "uk")

            // Value
            Assert.assertNotNull(stats2.value.value)
            Assert.assertNotNull(stats2.value.created)
            Assert.assertNotNull(stats2.value.updated)
            Assert.assertEquals(stats2.value.count, 2)

            // Reads
            Assert.assertEquals(2, stats2.hits.count)
            Assert.assertNotNull(stats2.hits.updated)
            Assert.assertTrue(stats2.hits.updated!! >= timestamp1)

            // Events
            Assert.assertNotNull(event)
            Assert.assertEquals(CacheAction.Expire, event?.action)
            Assert.assertEquals("countries", event?.key)
            Assert.assertTrue(!event?.uuid.isNullOrEmpty())
            Assert.assertEquals("kiit.app.cache.app.${CacheAction.Expire.name}.countries", event?.name ?: "")
        }
    }
}

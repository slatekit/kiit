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
package test

import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test
import slatekit.cache.*
import slatekit.common.DateTime
import slatekit.common.log.LoggerConsole


class Cache_Channel_Tests {

    fun getCache(initialize:Boolean = true, settings: CacheSettings = CacheSettings(10), listener:((CacheEvent) -> Unit)? = null): SimpleAsyncCache {
        val cache = SimpleAsyncCache.of("unit-tests-cache", LoggerConsole(), settings, listener)
        return cache
    }

    fun runTest(initialize:Boolean = true, op:suspend (AsyncCache) -> Unit) {
        val scope = CoroutineScope(Dispatchers.IO)
        runBlocking {
            val cache = getCache(initialize = false)
            if(initialize) {
                scope.launch {
                    cache.put("countries", "countries supported for mobile app", 60) { listOf("us", "ca") }
                }
            }
            //cache.respond()
            scope.launch {
                cache.work()
            }
            op(cache)
            cache.stop()
        }
    }

    @Test
    fun can_init() {
        runTest { cache ->
            val stats = cache.stats()
            Assert.assertEquals(1, stats.size)
        }
    }


    @Test
    fun can_put() {
        runBlocking {
            val timestamp = DateTime.now()
            var event:CacheEvent? = null
            val listener = { ev:CacheEvent -> event = ev }
            val cache = getCache(listener = listener)
            val countries1Deferred = cache.getAsync<List<String>>("countries")

            cache.poll()
            val countries1 = countries1Deferred.await()

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
            Assert.assertEquals(stats[0].error.applied, 0)

            // Value
            Assert.assertNotNull(stats[0].value.value)
            Assert.assertNotNull(stats[0].value.created)
            Assert.assertNotNull(stats[0].value.updated)
            Assert.assertEquals(stats[0].value.applied, 1)

            // Reads
            Assert.assertEquals(1, stats[0].hits.count)
            Assert.assertNotNull(stats[0].hits.timestamp)
            Assert.assertTrue(stats[0].hits.timestamp!! >= timestamp)

            // Expiry
            Assert.assertEquals(60, stats[0].expiry.seconds)
            Assert.assertTrue(stats[0].expiry.isAlive())
            Assert.assertTrue(timestamp <= stats[0].expiry.started)
            Assert.assertTrue(stats[0].expiry.expires > stats[0].expiry.started)

            // Events
            Assert.assertNotNull(event)
            Assert.assertEquals(CacheAction.Create, event?.action)
            Assert.assertEquals(cache.name, event?.origin)
            Assert.assertEquals("countries", event?.key)
            Assert.assertTrue(!event?.uuid.isNullOrEmpty())
            Assert.assertEquals("async-cache.${CacheAction.Create.name}.countries", event?.name ?: "")
        }
    }


    @Test
    fun can_get() {
        // Get 1
        runBlocking {
            val timestamp1 = DateTime.now()
            val cache = getCache()
            val countries1Deferred = cache.getAsync<List<String>>("countries")
            cache.poll()
            val countries1 = countries1Deferred.await()


            // Check values
            Assert.assertTrue(countries1!!.size == 2)
            Assert.assertTrue(countries1[0] == "us")
            Assert.assertTrue(countries1[1] == "ca")

            // Reads
            val stats1 = cache.stats()
            Assert.assertEquals(1, stats1[0].hits.count)
            Assert.assertNotNull(stats1[0].hits.timestamp)
            Assert.assertTrue(stats1[0].hits.timestamp!! >= timestamp1)

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
            Assert.assertNotNull(stats2[0].hits.timestamp)
            Assert.assertTrue(stats2[0].hits.timestamp!! >= timestamp1)
        }
    }


    @Test
    fun can_check() {
        runBlocking {
            val cache = getCache()
            Assert.assertTrue(cache.contains("countries"))
            Assert.assertFalse(cache.contains("promocodes"))
        }
    }


    @Test
    fun can_key() {
        runBlocking {
            val cache = getCache()
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


    @Test
    fun can_clear() {
        runBlocking {
            var event: CacheEvent? = null
            val listener = { ev: CacheEvent -> event = ev }
            val cache = getCache(listener = listener)
            cache.put("promocode", "promotion code", 60) { "promo-123" }

            runBlocking {
                cache.poll()
            }

            Assert.assertEquals(2, cache.keys().size)
            Assert.assertEquals(2, cache.size())

            cache.deleteAll()
            cache.poll()


            Assert.assertEquals(0, cache.keys().size)
            Assert.assertEquals(0, cache.size())

            // Events
            Assert.assertNotNull(event)
            Assert.assertEquals(CacheAction.DeleteAll, event?.action)
            Assert.assertEquals(cache.name, event?.origin)
            Assert.assertEquals("*", event?.key)
            Assert.assertTrue(!event?.uuid.isNullOrEmpty())
            Assert.assertEquals("async-cache.${CacheAction.DeleteAll.name}.*", event?.name ?: "")
        }
    }


    @Test
    fun can_set() {
        runBlocking {
            val timestamp = DateTime.now()
            var event:CacheEvent? = null
            val listener = { ev:CacheEvent -> event = ev }
            val cache = getCache(listener = listener)
            val countries1Future = cache.getAsync<List<String>>("countries")
            cache.poll()
            val countries1 = countries1Future.await()

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
            Assert.assertEquals(stats1[0].value.applied, 1)

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
            Assert.assertEquals(stats2[0].value.applied, 2)
        }
    }


    @Test
    fun can_refresh() {
        runBlocking {
            val timestamp = DateTime.now()
            var event: CacheEvent? = null
            val listener = { ev: CacheEvent -> event = ev }
            val cache = getCache(initialize = false, listener = listener)
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


    @Test
    fun can_expire() {
        runBlocking {
            val timestamp1 = DateTime.now()
            var event: CacheEvent? = null
            val listener = { ev: CacheEvent -> event = ev }
            val cache = getCache(initialize = false, listener = listener)
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
            val countries1Deferred = cache.getAsync<List<String>>("countries")

            cache.poll()
            val countries1 = countries1Deferred.await()

            // Check values
            Assert.assertTrue(countries1!!.size == 2)
            Assert.assertTrue(countries1[0] == "us")
            Assert.assertTrue(countries1[1] == "ca")

            // Reads
            val stats1 = cache.stats().first { it.key == "countries" }
            Assert.assertEquals(1, stats1.hits.count)
            Assert.assertNotNull(stats1.hits.timestamp)
            Assert.assertTrue(stats1.hits.timestamp!! >= timestamp1)

            cache.expire("countries")
            cache.poll()

            // Get 2
            val countries2Deferred = cache.getOrLoadAsync<List<String>>("countries")

            cache.poll()
            val countries2 = countries2Deferred.await()

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
            Assert.assertEquals(stats2.value.applied, 2)

            // Reads
            Assert.assertEquals(2, stats2.hits.count)
            Assert.assertNotNull(stats2.hits.timestamp)
            Assert.assertTrue(stats2.hits.timestamp!! >= timestamp1)

            // Events
            Assert.assertNotNull(event)
            Assert.assertEquals(CacheAction.Expire, event?.action)
            Assert.assertEquals(cache.name, event?.origin)
            Assert.assertEquals("countries", event?.key)
            Assert.assertTrue(!event?.uuid.isNullOrEmpty())
            Assert.assertEquals("async-cache.${CacheAction.Expire.name}.countries", event?.name ?: "")
        }
    }
}

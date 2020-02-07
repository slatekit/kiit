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

import org.junit.Assert
import org.junit.Test
import slatekit.cache.*
import slatekit.common.DateTime


class Cache_Sync_Tests {

    fun getCache(initialize:Boolean = true, synced:Boolean = true, settings: CacheSettings = CacheSettings(10)): SyncCache {
        val raw =  SimpleCache("sync-cache", settings = settings)
        val cache = if(synced) SimpleSyncCache(raw) else raw
        if(initialize) {
            cache.put("countries", "countries supported for mobile app", 60) { listOf("us", "ca") }
        }
        return cache
    }


    @Test
    fun can_init() {
        val cache = getCache(initialize = false)
        val stats = cache.stats()
        Assert.assertEquals(0, stats.size)
    }


    @Test
    fun can_put() {
        val timestamp = DateTime.now()
        val cache = getCache()
        val countries = cache.get<List<String>>("countries")

        // Check values
        Assert.assertTrue(countries!!.size == 2)
        Assert.assertTrue(countries[0] == "us")
        Assert.assertTrue(countries[1] == "ca")

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
        Assert.assertTrue(timestamp <= stats[0].expiry.started )
        Assert.assertTrue(stats[0].expiry.expires > stats[0].expiry.started)
    }


    @Test
    fun can_get() {
        val timestamp1 = DateTime.now()
        val cache = getCache()

        // Get 1
        val countries1 = cache.get<List<String>>("countries")

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
        val countries2 = cache.get<List<String>>("countries")

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


    @Test
    fun can_check() {
        val cache = getCache()
        Assert.assertTrue(cache.contains("countries"))
        Assert.assertFalse(cache.contains("promocodes"))
    }


    @Test
    fun can_key() {
        val cache = getCache()
        cache.put("promocode", "promotion code", 60) { "promo-123" }

        // Keys
        val keys = cache.keys()
        Assert.assertTrue(keys.contains("countries"))
        Assert.assertTrue(keys.contains("promocode"))

        // Size
        val size = cache.size()
        Assert.assertEquals(2, size)
    }


    @Test
    fun can_clear() {
        val cache = getCache()
        cache.put("promocode", "promotion code", 60) { "promo-123" }

        Assert.assertEquals(2, cache.keys().size)
        Assert.assertEquals(2, cache.size())

        cache.deleteAll()

        Assert.assertEquals(0, cache.keys().size)
        Assert.assertEquals(0, cache.size())
    }


    @Test
    fun can_set() {
        val timestamp = DateTime.now()
        val cache = getCache()
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
        Assert.assertEquals(stats1[0].value.applied, 1)

        cache.set("countries", listOf("us", "ca", "uk"))
        val countries2 = cache.get<List<String>>("countries")

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


    @Test
    fun can_refresh() {
        val timestamp = DateTime.now()
        val cache = getCache(initialize = false)
        var count = 0
        cache.put("countries", "countries supported for mobile app", 300) {
            if(count == 0) {
                count++
                listOf("us", "ca")
            } else {
                listOf("us", "ca", "uk")
            }
        }

        val countries1 = cache.get<List<String>>("countries")

        // Check values
        Assert.assertTrue(countries1!!.size == 2)
        Assert.assertTrue(countries1[0] == "us")
        Assert.assertTrue(countries1[1] == "ca")

        // Check values after update
        val countries2 = cache.getFresh<List<String>>("countries")
        Assert.assertTrue(countries2!!.size == 3)
        Assert.assertTrue(countries2[0] == "us")
        Assert.assertTrue(countries2[1] == "ca")
        Assert.assertTrue(countries2[2] == "uk")
    }


    @Test
    fun can_invalidate() {
        val timestamp1 = DateTime.now()
        val cache = getCache(initialize = false)
        var count = 0
        cache.put("countries", "countries supported for mobile app", 300) {
            if(count == 0) {
                count++
                listOf("us", "ca")
            } else {
                listOf("us", "ca", "uk")
            }
        }
        cache.put("promocode", "promotion code", 300) { "promo-123" }

        // Get 1
        val countries1 = cache.get<List<String>>("countries")

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
        Assert.assertEquals(stats2.value.applied, 2)

        // Reads
        Assert.assertEquals(2, stats2.hits.count)
        Assert.assertNotNull(stats2.hits.timestamp)
        Assert.assertTrue(stats2.hits.timestamp!! >= timestamp1)
    }
}

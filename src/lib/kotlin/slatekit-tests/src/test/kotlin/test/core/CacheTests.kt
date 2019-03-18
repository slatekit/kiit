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

import org.junit.Test
import slatekit.core.cache.Cache
import slatekit.core.cache.CacheSettings


class CacheTests {

/*
    @Test fun can_put_item() {
      val cache = Cache(CacheSettings(10))
      cache.put("countries", "countries supported for mobile app", 60, { ->
        listOf("us", "ca")
      })

      Thread.sleep(300)
      val result = cache.get<List<String>>("countries")

      assert(result!!.size == 2)
      assert(result[0] == "us")
      assert(result[1] == "ca")
    }


    @Test fun can_get_or_load_item() {
      val cache = Cache(CacheSettings(10))
      cache.put("countries", "countries supported for mobile app", 30, { ->
        listOf("us", "ca")
      })

      Thread.sleep(300)
      val result = cache.get<List<String>>("countries")
      assert(result!!.size == 2)
      assert(result!![0] == "us")
      assert(result!![1] == "ca")
    }


    @Test fun can_refresh_item() {
      val cache = Cache(CacheSettings(10))
      cache.put("countries", "countries supported for mobile app", 30, { ->
        listOf("us", "ca")
      })

      Thread.sleep(400)
      val result1 = cache.getCacheItem("countries")

      // Refresh
      cache.refresh("countries")
      Thread.sleep(300)
      val result2 = cache.getCacheItem("countries")

      assert(result1 != null )
      assert(result2 != null )
      assert(result1!!.updated!! < result2!!.updated!!)
    }


    @Test fun can_refresh_and_get_result() {
      val cache = Cache(CacheSettings(10))
      cache.put("countries", "countries supported for mobile app", 30, { ->
        listOf("us", "ca")
      })

      Thread.sleep(400)
      val result1 = cache.getCacheItem("countries")

      // Refresh
      val result = cache.getFresh<List<String>>("countries")

      val result2 = cache.getCacheItem("countries")
      assert(result1 != null)
      assert(result2 != null)
      assert(result1!!.updated!! < result2!!.updated!!)

      Thread.sleep(300)
    }
    */
}

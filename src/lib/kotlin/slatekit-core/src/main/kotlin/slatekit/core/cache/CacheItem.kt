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

package slatekit.core.cache

import slatekit.common.DateTime

/**
 * Represents a single cache item
 * @param key        : The name of the cache key
 * @param value      : The last known value of the cache
 * @param text       : The last known text value
 * @param updated    : The last time it was updated
 * @param seconds    : The time in seconds of its expiry
 * @param expires    : The time it will expire
 * @param error      : The last error when fetching
 * @param errorCount : The total number of errors when fetching
 * @param accessed   : The last time it was accessed
 * @param accessCount: The amount of times it was accessed
 */
data class CacheItem(
                        val key         : String,
                        val value       : Any?,
                        val text        : String?,
                        val updated     : DateTime?,
                        val seconds     : Int,
                        val expires     : DateTime,
                        val error       : Throwable?,
                        val errorCount  : Int,
                        val accessed    : DateTime?,
                        val accessCount : Long?
                     )
{

  fun isExpired(): Boolean = if (seconds <= 0) false else expires < DateTime.now()


  /**
   * whether or not this entry is still alive in terms of its expiration date
   * @return
   */
  fun isAlive(): Boolean = !isExpired()
}

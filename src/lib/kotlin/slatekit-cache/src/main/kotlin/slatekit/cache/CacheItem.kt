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

package slatekit.cache

import slatekit.common.DateTime
import slatekit.tracking.Tracked

/**
 * Represents a single cache item
 * @param key : The name of the cache key
 * @param text : The last known text value
 * @param expiryInSeconds : The time in seconds of its expiry
 * @param expires : The time it will expire
 * @param value : The last known value of the cache
 * @param errored : The last error when fetching
 * @param accessed : The last time it was accessed
 */
data class CacheItem(
        val key: String,
        val text: String?,
        val expiryInSeconds: Int,
        val expires: DateTime,
        val value: Tracked<Any>,
        val errored: Tracked<Throwable>,
        val accessed: Tracked<Unit>
) {

  fun isExpired(): Boolean = if (expiryInSeconds <= 0) false else expires < DateTime.now()

  /**
   * whether or not this entry is still alive in terms of its expiration date
   * @return
   */
  fun isAlive(): Boolean = !isExpired()
}

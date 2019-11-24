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

import kotlinx.coroutines.runBlocking
import slatekit.common.DateTime
import slatekit.tracking.Tracked
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * A container for a cache item with additional logic for handling
 *  1. refreshing
 *  2. errors on refresh
 *  3. metrics on refresh
 *  4. concurrency via AtomicReference
 *
 * @param key : The key of the cache item for lookup
 * @param text : A text value for the item ( for description purposes )
 * @param seconds : Amount of time in seconds until it expires
 * @param fetcher : The function that can actually fetch the cache data
 */
data class CacheEntry(
    val key: String,
    val desc: String,
    val text: String?,
    val seconds: Int,
    val fetcher: suspend () -> Any?
) {
    /**
     * The last time this was accessed.
     *
     * NOTE: This is separated from the data of the cache item itself ( CacheItem )
     * because this is designed for heavy reads, the access count/date is more frequently
     * updated while the item is updated only when its refreshed
     */
    val accessed = AtomicReference<DateTime>(DateTime.now())

    /**
     * The total number of times accessed
     *
     * NOTE: This is separated from the data of the cache item itself ( CacheItem )
     * because, this is designed for heavy reads, the access count/date is more frequently
     * updated while the item is updated only when its refreshed.
     */
    val accessCount = AtomicLong(0)

    /**
     * The actual cache item which is updatd only when its refreshed.
     */
    val item = AtomicReference<CacheItem>(
            CacheItem(key, null, seconds, DateTime.now().plusSeconds(seconds.toLong()), Tracked(), Tracked(), Tracked())
    )

    /**
     * increments the last account time and access counts
     * @return
     */
    fun inc(): Long {

        // This is built for heavy reads ( not writes ), so we don't
        // really care if the another thread updated it because
        // this provides an REASONABLE summary of the last access time/counts
        accessed.set(DateTime.now())
        val count = accessCount.incrementAndGet()
        if (count > Long.MAX_VALUE - 10000)
            accessCount.set(0L)
        return accessCount.get()
    }

    /**
     * This can only be called during a failed refresh.
     */
    fun error(ex: Throwable) {
        item.get().errored.set(ex)
    }

    /**
     * This is called on successful refresh of the cache
     * @param result
     */
    fun success(result: Any?, text:String? = null) {
        val original = item.get()
        val timestamp = DateTime.now()
        original.accessed.set(Unit)
        original.value.set(result)
        val updated = original.copy(
                text = text ?: "",
                expires = timestamp.plusSeconds(original.expiryInSeconds.toLong())
        )

        // Update to  value
        item.set(updated)
    }

    /**
     * invalidates the current item by setting its expiry to to current time
     */
    fun invalidate() {
        val copy = item.get().copy(expires = DateTime.now())
        return item.set(copy)
    }

    /**
     * Refreshes this cache item
     */
    fun refresh() {
        try {
            val result = runBlocking {
                fetcher()
            }
            val content = result?.toString() ?: ""
            success(result, content)
        } catch (ex: Exception) {
            error(ex)
        }
    }

    fun set(value:Any?, text: String?){
        success(value, text)
    }

    /**
     * whether or not this cache item has expired
     * @return
     */
    fun isExpired(): Boolean = item.get().isExpired()

    /**
     * whether or not this entry is still alive in terms of its expiration date
     * @return
     */
    fun isAlive(): Boolean = !isExpired()
}

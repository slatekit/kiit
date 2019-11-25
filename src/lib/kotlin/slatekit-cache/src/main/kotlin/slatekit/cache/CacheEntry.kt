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
import slatekit.tracking.Expiry
import slatekit.tracking.Fetches
import slatekit.tracking.Tracked
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
    val seconds: Int,
    val fetcher: suspend () -> Any?
) {
    /**
     * The actual cache item which is updatd only when its refreshed.
     */
    val item = AtomicReference<CacheValue>(
            CacheValue(
                text = null,
                expiry = Expiry(seconds.toLong()),
                reads = Fetches(20),
                value = Tracked(),
                error = Tracked())
    )


    /**
     * This can only be called during a failed refresh.
     */
    fun failure(ex: Throwable) {
        item.get().error.set(ex)
    }

    /**
     * This is called on successful refresh of the cache
     * @param result
     */
    fun success(result: Any?, text:String? = null) {
        val curr = item.get()
        val next = curr.update(result, text)
        item.set(next)
    }

    /**
     * invalidates the current item by setting its expiry to to current time
     */
    fun expire() {
        val expired = item.get().expire()
        return item.set(expired)
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
            failure(ex)
        }
    }

    fun stats():CacheStats {
        val item = item.get()
        return CacheStats(
            key = key,
            expiry = item.expiry,
            reads = item.reads.get(),
            value = item.value.get().get(),
            error = item.error.get().get())
    }

    fun set(value:Any?, text: String?){
        success(value, text)
    }

    /**
     * whether or not this entry is still alive in terms of its expiration date
     * @return
     */
    fun isAlive(): Boolean = item.get().expiry.isAlive()
}

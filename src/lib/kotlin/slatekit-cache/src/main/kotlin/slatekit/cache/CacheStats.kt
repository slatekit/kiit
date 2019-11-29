package slatekit.cache

import slatekit.tracking.Expiry
import slatekit.tracking.Fetched
import slatekit.tracking.Updated

/**
 * Provides a copy of the CacheValue
 */
data class CacheStats(val key:String,
                      val expiry: Expiry,
                      val hits  : Fetched,
                      val reads : Fetched?,
                      val misses: Fetched?,
                      val value : Updated<Any>,
                      val error : Updated<Throwable>)

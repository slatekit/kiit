package kiit.cache

import slatekit.telemetry.Expiry
import slatekit.telemetry.Tracked

/**
 * Provides a copy of the CacheValue
 */
data class CacheStats(val key:String,
                      val expiry: Expiry,
                      val hits  : Tracked<Any>,
                      val reads : Tracked<Any>?,
                      val misses: Tracked<Any>?,
                      val value : Tracked<Any>,
                      val error : Tracked<Throwable>)

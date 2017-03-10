/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.cache

import slate.common.DateTime

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
case class CacheItem(
                        key         : String,
                        value       : Option[Any],
                        text        : Option[String],
                        updated     : Option[DateTime],
                        seconds     : Int,
                        expires     : DateTime,
                        error       : Option[Throwable],
                        errorCount  : Int,
                        accessed    : Option[DateTime],
                        accessCount : Option[Long]
                     )
{

  def isExpired(): Boolean = if(seconds <= 0 ) false else expires < DateTime.now


  /**
   * whether or not this entry is still alive in terms of its expiration date
   * @return
   */
  def isAlive(): Boolean = !isExpired()
}

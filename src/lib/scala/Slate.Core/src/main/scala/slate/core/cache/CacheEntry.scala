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

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}
import slate.common.DateTime
import slate.common.DateTime._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure, Try}


/**
  * A container for a cache item with additional logic for handling
  *  1. refreshing
  *  2. errors on refresh
  *  3. metrics on refresh
  *  4. concurrency via AtomicReference
  *
  * @param key          : The key of the cache item for lookup
  * @param text         : A text value for the item ( for description purposes )
  * @param seconds      : Amount of time in seconds until it expires
  * @param fetcher      : The function that can actually fetch the cache data
  */
private case class CacheEntry(
                          key         : String,
                          text        : Option[String],
                          seconds     : Int,
                          fetcher     : () => Option[Any]
                         )
{
  /**
   * The last time this was accessed.
   *
   * NOTE: This is separated from the data of the cache item itself ( CacheItem )
   * because this is designed for heavy reads, the access count/date is more frequently
   * updated while the item is updated only when its refreshed
   */
  val accessed = new AtomicReference[DateTime](DateTime.now())


  /**
   * The total number of times accessed
   *
   * NOTE: This is separated from the data of the cache item itself ( CacheItem )
   * because, this is designed for heavy reads, the access count/date is more frequently
   * updated while the item is updated only when its refreshed.
   */
  val accessCount = new AtomicLong(0)


  /**
   * The actual cache item which is updatd only when its refreshed.
   */
  val item = new AtomicReference[CacheItem](
    CacheItem(key, None, text, None, seconds, now().addSeconds(seconds), None, 0, None, None)
  )


  /**
   * increments the last account time and access counts
   * @return
   */
  def increment(): Long = {

    // This is built for heavy reads ( not writes ), so we don't
    // really care if the another thread updated it because
    // this provides an REASONABLE summary of the last access time/counts
    accessed.set(DateTime.now())
    val count = accessCount.incrementAndGet()
    if ( count > Long.MaxValue - 10000)
      accessCount.set(0L)
    accessCount.get
  }


  /**
   * This can only be called during a failed refresh.
   */
  def error(ex:Throwable):Unit = {
    val original = item.get

    // Bump up errors.
    val updated = original.copy(
      errorCount = original.errorCount + 1,
      error = Option(ex)
    )

    // Update to new value
    item.set(updated)
  }


  /**
   * This is called on successful refresh of the cache
   * @param result
   */
  def success(result:Option[Any]):Unit = {
    val original = item.get

    val timestamp = DateTime.now()

    val updated = original.copy(
      value = result,
      updated = Some(timestamp),
      expires = timestamp.addSeconds(original.seconds),
      accessed = Some(timestamp),
      accessCount = Some(accessCount.get + 1)
    )

    // Update to new value
    item.set(updated)
  }


  /**
   * invalidates the current item by setting its expiry to to current time
   */
  def invalidate(): Unit = {
    val copy = item.get().copy(expires =  DateTime.now())
    item.set(copy)
  }


  /**
   * Refreshes this cache item
   */
  def refresh(implicit ctx:ExecutionContext ): Unit = {
    val f = Future {
      fetcher()
    }
    f.onComplete {
      case Failure(e) => error(e)
      case Success(s) => success(s)
    }
  }


  /**
   * whether or not this cache item has expired
   * @return
   */
  def isExpired(): Boolean = item.get.isExpired()


  /**
   * whether or not this entry is still alive in terms of its expiration date
   * @return
   */
  def isAlive(): Boolean = !isExpired()
}

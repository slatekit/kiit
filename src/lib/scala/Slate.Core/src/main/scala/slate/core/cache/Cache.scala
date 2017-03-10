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

import java.util.concurrent.ConcurrentHashMap
import slate.common.Funcs

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


/**
 * This light-weight implementation of a Cache ( LRU - Least recently used )
 * contains the following design approaches:
 *
 * 1. designed for low frequency writes
 * 2. Storing any type [T] item
 * 3. Storing a function, supplied by caller that can handle refreshing the cache
 * 4. allowing for invalidating a cache item
 * 5. allowing for invalidating all cache items
 * 6. designed to get or refresh an item asynchronously
 * 7. customizable expiration time per cache item
 *
 * @param settings
 */
class Cache(val settings:CacheSettings, implicit val ctx:ExecutionContext ) extends ICache {

  private val _lookup = new ConcurrentHashMap[String, CacheEntry].asScala


  /**
   * size of the cache
    *
    * @return
   */
  override def size():Int = _lookup.size


  /**
   * whether this cache contains the entry with the supplied key
    *
    * @param key
   * @return
   */
  override def contains(key: String): Boolean = _lookup.contains(key)


  /**
   * invalidates all the entries in this cache by maxing out their expiration times
   */
  override def invalidateAll(): Unit = _lookup.keys.toList.foreach( key => invalidate(key))


  /**
   * invalidates a specific cache item with the key
   */
  override def invalidate(key:String): Unit = {
    _lookup.get(key) match {
      case None    =>
      case Some(c) => c.invalidate()
    }
  }


  /**
   * remove a single cache item with the key
    *
    * @param key
   */
  override def remove(key:String): Boolean = _lookup.remove(key).fold(false)( c => true )


  /**
   * gets a cache item associated with the key
    *
    * @param key
   * @return
   */
  override def getCacheItem(key: String): Option[CacheItem] = {
    _lookup.get(key).map( c => c.item.get )
  }


  /**
    * gets a cache item associated with the key
    *
    * @param key
    * @tparam T
    * @return
    */
  override def get[T](key: String): Option[T] = {
    val result = _lookup.get(key) match {
      case None    => None
      case Some(c) => {
        if(c.isAlive()) {
          c.item.get().value.flatMap[T](a => Option(a.asInstanceOf[T]))
        }
        else {
          // Expired so kick off a refresh
          c.refresh(ctx)
          None
        }
      }
    }
    result
  }


  /**
   * gets a cache item or loads it if not available, via a future
    *
    * @param key
   * @tparam T
   * @return
   */
  override def getOrLoad[T](key: String): Future[Option[T]] = {
    val result = _lookup.get(key) match {
      case Some(c) => {
        if(c.isAlive()) {
          Future[Option[T]] { c.item.get().value.flatMap[T](a => Option(a.asInstanceOf[T])) }
        }
        else {
          getFresh[T](key)
        }
      }
      case _  => {
        Future[Option[T]] { None }
      }
    }
    result
  }


  /**
    * manual / explicit refresh of a cache item with a future result
    * in order to get the item
    *
    * @param key
    */
  override def getFresh[T](key: String): Future[Option[T]] = {
    _lookup.get(key) match {
      case Some(c) => {

        // Refresh via future
        val refresh = Future {
          c.fetcher().flatMap[T]( v => Option(v.asInstanceOf[T]) )
        }

        // Ensure we store the result of the refresh on our end
        refresh andThen {
          case Success(s) => {
            c.success(s)
          }
          case Failure(s) => {
            c.error(s)
          }
        }
      }
      case _       => {
        Future { None }
      }
    }
  }


  /**
    * gets a cache item associated with the key
    *
    * @param key
    * @return
    */
  override def refresh(key: String): Unit = {
    _lookup.get(key) match {
      case Some(c) => c.refresh(ctx)
      case _       =>
    }
  }


  /**
   * creates a new cache item
    *
    * @param key       : The name of the cache key
   * @param seconds   : The expiration time in seconds
   * @param fetcher   : The function to fetch the data ( will be wrapped in a Future )
   * @tparam T
   */
  override def put[T](key:String, text:Option[String], seconds:Option[Int], fetcher: () => Option[T]): Unit = {

    _lookup.get(key) match {
      case None    => {
        insert(key, text, seconds.getOrElse(-1), fetcher)
      }
      case Some(c) => {
        if(c.isExpired()) {
          c.refresh(ctx)
        }
        else {
          c.increment()
        }
      }
      case _  => {
        insert(key, text, seconds.getOrElse(-1), fetcher)
      }
    }
  }


  private def insert(key:String,
                     text:Option[String],
                     seconds:Int,
                     fetcher: () => Option[Any]): Unit = {
    val entry = new CacheEntry(key, text, seconds, fetcher )
    _lookup(key) = entry
    entry.refresh(ctx)
  }
}

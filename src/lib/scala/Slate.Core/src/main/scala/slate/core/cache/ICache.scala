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

import scala.concurrent.{ExecutionContext, Future}

trait ICache {

  /**
    * settings for the cache
    */
  val settings:CacheSettings


  /**
    * For futures
    */
  val ctx:ExecutionContext


  /**
    * number of items in the cache
    * @return
    */
  def size():Int


  /**
    * whether the cache contains the key
    * @param key
    * @return
    */
  def contains(key:String): Boolean


  /**
    * gets the cache entry itself.
    * NOTE: This is exposed
    * @param key
    * @return
    */
  def getCacheItem(key: String): Option[CacheItem]


  /**
    * gets an item from the cache if it exists and is alive
    * @param key
    * @tparam T
    * @return
    */
  def get[T](key:String) : Option[T]


  /**
    * gets an item from the cache as a future
    * @param key
    * @tparam T
    * @return
    */
  def getOrLoad[T](key:String) : Future[Option[T]]


  /**
    * manual / explicit refresh of a cache item with a future result
    * in order to get the item
    * @param key
    */
  def getFresh[T](key: String): Future[Option[T]]


  /**
    * puts an item in the cache and loads it immediately
    * @param key
    * @param desc
    * @param seconds
    * @param fetcher
    * @tparam T
    */
  def put[T](key:String, desc:Option[String], seconds:Option[Int], fetcher: () => Option[T]): Unit


  /**
    * removes a item from the cache
    * @param key
    * @return
    */
  def remove(key:String):Boolean


  /**
    * manual / explicit refresh of a cache item
    * @param key
    */
  def refresh(key: String): Unit


  /**
    * invalidates a single cache item by its key
    * @param key
    */
  def invalidate(key:String): Unit


  /**
    * invalidates all the cache items
    */
  def invalidateAll():Unit
}

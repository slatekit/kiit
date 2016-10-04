/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common


import scala.collection.mutable.{ListBuffer, Map}


class ListMap[K,V] {

  case class ListMapEntry(key:K, value:V, index:Int) { }


  protected val _map = Map[K, ListMapEntry]()
  protected val _list = ListBuffer[ListMapEntry]()


  /**
   * size of the list
   * @return
   */
  def size(): Int =
  {
    _list.size
  }


  /**
   * whether there is a key with the supplied name
   * @param key
   * @return
   */
  def contains(key:K):Boolean =
  {
    _map.contains(key)
  }


  def all(): List[V] =
  {
    val buffer = new ListBuffer[V]()
    for(ndx <- 0 until _list.size)
    {
      val entry = _list(ndx)
      buffer.append(entry.value)
    }
    buffer.toList
  }


  def keys(): List[K] =
  {
    if(_list.size == 0)
      return List[K]()

    val buffer = new ListBuffer[K]()
    for(ndx <- 0 until _list.size)
    {
      val entry = _list(ndx)
      buffer.append(entry.key)
    }
    buffer.toList
  }


  /**
   * adds a new key/value pair
   * @param key
   * @param value
   */
  def add(key:K, value:V):Unit =
  {
    if(contains(key))
    {
      remove(key)
    }

    val index = _list.size
    val entry = new ListMapEntry(key,value, index)
    _map += (key -> entry)
    _list += entry
  }


  /**
   * gets the value with the supplied key
   * @param key
   * @return
   */
  def apply(key:K):V =
  {
    if(!contains(key))
      throw new IllegalArgumentException("key : " + key + " not found")
    _map(key).value
  }


  /**
    * gets the value at the supplied index position.
    * @param pos
    * @return
    */
  def getAt(pos:Int):V =
  {
    Ensure.index(pos, _list.size, "index out of range for list map")
    _list(pos).value
  }


  /**
   * adds a key/value to this collection
   * @param key
   * @param value
   */
  def update(key:K, value:V) = add(key, value)


  /**
   * removes the key/value pair associated with the key
   * @param key
   */
  def remove(key:K): Unit =
  {
    if(!contains(key))
      return

    val entry = _map(key)
    _list.remove(entry.index, 1)
    _map.remove(key)
  }


  /**
   * iterates over each key/value pair and supplies it to the callback
   * @param callback
   */
  def each(callback:(Int,K,V) => Unit ): Unit =
  {
    for(ndx <- 0 until _list.size)
    {
      val entry = _list(ndx)
      callback(ndx, entry.key, entry.value)
    }
  }


  override def clone():ListMap[K,V] =
  {
    val copy = new ListMap[K,V]()
    for(ndx <- 0 until _list.size)
    {
      val entry = _list(ndx)
      copy.add(entry.key, entry.value)
    }
    copy
  }
}

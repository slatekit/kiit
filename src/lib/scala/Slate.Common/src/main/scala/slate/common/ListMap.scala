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

/**
 *
 * This class is meant for INTERNAL USE ONLY ( meaning only inside of slate-kit and not
 * for public consumption )
 *
 * @tparam A
 * @tparam B
 */
class ListMap[A,B] {

  case class ListMapEntry(key:A, value:B, index:Int) { }

  private val _list = ListBuffer[ListMapEntry]()
  private val _map = Map[A, ListMapEntry]()


  /**
   * size of the list
   * @return
   */
  def size(): Int = {  _list.size }


  /**
   * whether there is a key with the supplied name
   * @param key
   * @return
   */
  def contains(key:A):Boolean = { _map.contains(key) }


  /** Checks if this map maps `key` to a value and return the
    *  value if it exists.
    *
    *  @param  key the key of the mapping of interest
    *  @return     the value of the mapping, if it exists
    */
  def get(key: A): Option[B] = {
    if(contains(key))
      Some( _map(key).value )
    else
      None
  }


  /**
   * gets the value at the supplied index position.
   * @param pos
   * @return
   */
  def getAt(pos:Int): Option[B] = {
    if ( pos < 0 || pos >= _list.size )
      None
    else
      Some( _list(pos).value )
  }


  /**
   * Adds a new entry
   * @param kv
   * @return
   */
  def + (kv: (A, B)): ListMap[A, B] = update(kv._1, kv._2)


  /**
   * removes the key/value pair associated with the key
   * @param key
   */
  def -(key:A): ListMap[A,B] = remove(key)


  /**
   * adds a new key/value pair
   * @param key
   * @param value
   */
  def add(key:A, value:B):ListMap[A,B] = update(key, value)


  /**
   * adds a key/value to this collection
   * @param key
   * @param value
   */
  def update[B1 >: B](key:A, value:B1):ListMap[A,B] = {
    if(contains(key))
    {
      remove(key)
    }

    val index = _list.size
    val entry = new ListMapEntry(key,value.asInstanceOf[B], index)
    _map(key) =  entry
    _list.append(entry)
    this
  }


  /**
   * removes the key/value pair associated with the key
   * @param key
   */
  def remove(key:A): ListMap[A,B] =
  {
    if(!contains(key))
      return this

    val entry = _map(key)
    _list.remove(entry.index, 1)
    _map.remove(key)
    this
  }


  def keys(): List[A] =
  {
    if(_list.size == 0)
      return List[A]()

    val buffer = new ListBuffer[A]()
    for(ndx <- 0 until _list.size)
    {
      val entry = _list(ndx)
      buffer.append(entry.key)
    }
    buffer.toList
  }


  def all(): List[B] = values()


  def values(): List[B] =
  {
    val buffer = new ListBuffer[B]()
    for(ndx <- 0 until _list.size)
    {
      val entry = _list(ndx)
      buffer.append(entry.value)
    }
    buffer.toList
  }


  /**
   * retrieves the value with the supplied key
   * @param key
   * @return
   */
  def apply(key:A):B =
  {
    if(!contains(key))
      throw new IllegalArgumentException("key : " + key + " not found")
    _map(key).value
  }


  /**
   * iterates over each key/value pair and supplies it to the callback
   * @param callback
   */
  def each(callback:(Int,A,B) => Unit ): Unit =
  {
    for(ndx <- 0 until _list.size)
    {
      val entry = _list(ndx)
      callback(ndx, entry.key, entry.value)
    }
  }


  override def clone():ListMap[A,B] =
  {
    val copy = new ListMap[A,B]()
    for(ndx <- _list.indices)
    {
      val entry = _list(ndx)
      copy.add(entry.key, entry.value)
    }
    copy
  }

}

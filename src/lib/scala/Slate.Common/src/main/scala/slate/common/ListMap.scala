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


object ListMap {

  def build[A,B](items:Option[List[(A,B)]]):ListMap[A,B] = {
    val lm = new ListMap[A,B]()
    items.fold(Unit)( all => {
      all.foreach( item => {
        lm.add(item._1, item._2)
      })
      Unit
    })
    lm
  }
}



/**
 *
 * This class is meant for INTERNAL USE ONLY ( meaning only inside of slate-kit and not
 * for public consumption )
 *
 * @tparam A
 * @tparam B
 */
class ListMap[A,B] {

  case class ListMapEntry(key:A, value:B) { }

  private val _list = ListBuffer[ListMapEntry]()
  private val _map = Map[A, Int]()


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
  def get(key: A): B = _list(_map(key)).value


  /**
   * gets the value at the supplied index position.
   * @param pos
   * @return
   */
  def getAt(pos:Int): B = _list(pos).value


  /** Checks if this map maps `key` to a value and return the
    *  value if it exists.
    *
    *  @param  key the key of the mapping of interest
    *  @return     the value of the mapping, if it exists
    */
  def getOpt(key: A): Option[B] = {
    if(contains(key))
      Some( _list(_map(key)).value )
    else
      None
  }


  /**
    * gets the value at the supplied index position.
    * @param pos
    * @return
    */
  def getAtOpt(pos:Int): Option[B] = {
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
    val entry = new ListMapEntry(key,value.asInstanceOf[B])
    _map(key) =  index
    _list.append(entry)
    this
  }


  /**
   * removes the key/value pair associated with the key
   * @param key
   */
  def remove(key:A): ListMap[A,B] =
  {
    if(contains(key)) {
      val index = _map(key)
      _list.remove(index, 1)
      _map.remove(key)
      remap()
    }
    this
  }


  def keys(): List[A]   = _list.map( i => i.key).toList


  def values(): List[B] = _list.map( i => i.value).toList


  def all(): List[B] = values()


  def asMap(): scala.collection.immutable.Map[String,Any] = {
    _list.map( a => a.key.toString -> a.value.asInstanceOf[Any] ).toMap
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
    _list(_map(key)).value
  }


  /**
   * iterates over each key/value pair and supplies it to the callback
   * @param callback
   */
  def each(callback:(Int,A,B) => Unit ): Unit =
  {
    _list.indices.foreach( ndx => {
      val entry = _list(ndx)
      callback(ndx, entry.key, entry.value)
    })
  }


  override def clone():ListMap[A,B] =
  {
    val copy = new ListMap[A,B]()
    _list.indices.foreach( ndx => {
      val entry = _list(ndx)
      copy.add(entry.key, entry.value)
    })
    copy
  }


  private def remap():Unit = {

    _list.indices.foreach( ndx => {
      val item = _list(ndx)
      _map(item.key) = ndx
    })
  }

}

/*


case class DtValMap[T](iv: Map[DateTime, T]) extends Map[DateTime, T] {
  def -(key: DateTime) = new DtValMap(iv.-(key))
  def get(key: DateTime) = iv.get(key)
  def +[T1 >: T](kv: (DateTime, T1)): DtValMap[T1] = new DtValMap(iv + kv)
  def iterator: Iterator[(DateTime, T)] = iv.iterator
}
* */

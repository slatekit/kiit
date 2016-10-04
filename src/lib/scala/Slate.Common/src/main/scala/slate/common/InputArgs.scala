/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.common

import scala.collection.mutable.Map

class InputArgs(protected val _map:Map[String,Any]) extends Inputs {

  /**
    * adds a key/value to this collection
    *
    * @param key
    * @param value
    */
  def update(key:String, value:String):Unit =
  {
    _map(key) =  value
  }


  /// <summary>
  override def getValue(key: String): AnyVal =
  {
    if ( !containsKey(key) )
      throw new IllegalArgumentException("key not found in arguments : " + key)

    _map(key).asInstanceOf[AnyVal]
  }


  /// <summary>
  override def getObject(key: String): AnyRef =
  {
    if ( !containsKey(key) ) return null

    _map(key).asInstanceOf[AnyRef]
  }


  /// <summary>
  override def containsKey(key: String): Boolean =
  {
    _map.contains(key)
  }


  override def size(): Int = {
    _map.size
  }
}

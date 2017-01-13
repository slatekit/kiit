/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.conf


import slate.common.encrypt.Encryptor

import scala.collection.mutable.Map


class Config(
              private val _map:Map[String,String],
              encryptor:Option[Encryptor] = None
              )
  extends ConfigBase(encryptor) {

  override def raw:Any = _map


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
    if ( !containsKey(key) )
      null
    else
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

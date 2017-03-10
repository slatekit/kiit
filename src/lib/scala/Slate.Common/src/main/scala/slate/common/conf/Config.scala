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

import slate.common.{InputFuncs, DateTime}

import scala.collection.mutable.Map


class Config( private val _map:Map[String,String],
              decryptor:Option[(String) => String] = None
            )
  extends ConfigBase(decryptor) {

  override def getString   (key: String) : String   = InputFuncs.decrypt(_map(key), decryptor)
  override def getDate     (key: String) : DateTime = InputFuncs.convertDate(_map(key))
  override def getBool     (key: String) : Boolean  = _map(key).toBoolean
  override def getInt      (key: String) : Int      = _map(key).toInt
  override def getLong     (key: String) : Long     = _map(key).toLong
  override def getDouble   (key: String) : Double   = _map(key).toDouble
  override def getFloat    (key: String) : Float    = _map(key).toFloat

  override def raw:Any = _map
  override def get(key: String) : Option[Any]             = if (_map.contains(key)) Option(_map(key)) else None
  override def getObject(key: String): Option[AnyRef]     = if (_map.contains(key)) Option(_map(key).asInstanceOf[AnyRef]) else None
  override def containsKey(key: String): Boolean          = _map.contains(key)
  override def size(): Int                                = _map.size
}

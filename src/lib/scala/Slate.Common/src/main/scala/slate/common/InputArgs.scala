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


class InputArgs(protected val _map:Map[String,Any],
                private val _decryptor:Option[(String) => String] = None) extends Inputs {

  override def getString   (key: String) : String   = InputFuncs.decrypt(_map(key).toString, _decryptor)
  override def getBool     (key: String) : Boolean  = _map(key).toString.toBoolean
  override def getInt      (key: String) : Int      = _map(key).toString.toInt
  override def getLong     (key: String) : Long     = _map(key).toString.toLong
  override def getDouble   (key: String) : Double   = _map(key).toString.toDouble
  override def getFloat    (key: String) : Float    = _map(key).toString.toFloat
  override def getDate     (key: String) : DateTime = {
    _map(key) match {
      case d:DateTime => d
      case s:String   => InputFuncs.convertDate(s)
      case n:Long     => DateTime.parseNumericDate12(n.toString)
    }
  }

  override def get(key: String) : Option[Any]             = if (_map.contains(key)) Option(_map(key)) else None
  override def getObject(key: String): Option[AnyRef]     = if (_map.contains(key)) Option(_map(key).asInstanceOf[AnyRef]) else None
  override def containsKey(key: String): Boolean          = _map.contains(key)
  override def size(): Int                                = _map.size
}

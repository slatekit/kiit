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

abstract class Inputs {

  /**
    * gets the value with the supplied key
    *
    * @param key
    * @return
    */
  def apply(key:String):Any =
  {
    if(!containsKey(key))
      throw new IllegalArgumentException("key : " + key + " not found")
    get(key)
  }


  def get(key: String)       : Any = getObjectAs[String](key)


  def getString(key: String) : String = getObjectAs[String](key)


  def getBool(key: String)   : Boolean = getValueAs[Boolean](key)


  def getInt(key: String)    : Int = getValueAs[Int](key)


  def getLong(key: String)   : Long = getValueAs[Long](key)


  def getDouble(key: String) : Double = getValueAs[Double](key)


  def getDate(key: String)   : DateTime = getValueAs[DateTime](key)


  def getStringOrElse(key: String, defaultVal:String) : String =
  {
    getObjectOrElse[String](key, defaultVal)
  }


  def getBoolOrElse(key: String, defaultVal: Boolean): Boolean =
  {
    getValueOrElse[Boolean](key, defaultVal)
  }


  def getIntOrElse(key: String, defaultVal: Int) : Int =
  {
    getValueOrElse[Int](key, defaultVal)
  }


  def getLongOrElse(key: String, defaultVal: Long) : Long =
  {
    getValueOrElse[Long](key, defaultVal)
  }


  def getDoubleOrElse(key: String, defaultVal: Double) : Double =
  {
    getValueOrElse[Double](key, defaultVal)
  }


  def getDateOrElse(key: String, defaultVal: DateTime) : DateTime =
  {
    getObjectOrElse[DateTime](key, defaultVal)
  }


  def getValueAs[T](key: String): T =
  {
    getValue(key).asInstanceOf[T]
  }


  def getValueOrElse[T](key: String, defaultVal:T): T =
  {
    if (!containsKey(key))
      defaultVal
    else
      getValueAs[T](key)
  }


  def getObjectAs[T >: Null](key: String): T =
  {
    getObject(key).asInstanceOf[T]
  }


  def getObjectOrElse[T >: Null](key: String, defaultVal:T): T =
  {
    if (!containsKey(key))
      defaultVal
    else
      getObject(key).asInstanceOf[T]
  }


  def getValue(key: String): AnyVal


  def getObject(key: String): AnyRef


  def contains(key:String): Boolean = containsKey(key)


  def containsKey(key: String): Boolean


  def size(): Int
}
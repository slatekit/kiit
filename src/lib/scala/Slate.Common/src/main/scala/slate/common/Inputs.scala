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


  def get(key: String) : Any =
  {
    getObjectAs[String](key)
  }


  def getString(key: String) : String =
  {
    getObjectAs[String](key)
  }


  def getBool(key: String) : Boolean =
  {
    getValueAs[Boolean](key)
  }


  def getInt(key: String) : Int =
  {
    getValueAs[Int](key)
  }


  def getLong(key: String): Long =
  {
    getValueAs[Long](key)
  }


  def getDouble(key: String): Double =
  {
    getValueAs[Double](key)
  }


  def getDate(key: String): DateTime =
  {
    getValueAs[DateTime](key)
  }


  def getStringOrElse(key: String, defaultVal:String) : String =
  {
    if (!containsKey(key))
      return defaultVal

    getObjectAs[String](key)
  }


  def getBoolOrElse(key: String, defaultVal: Boolean): Boolean =
  {
    if (!containsKey(key))
      return defaultVal

    getValueAs[Boolean](key)
  }


  def getIntOrElse(key: String, defaultVal: Int) : Int =
  {
    if (!containsKey(key))
      return defaultVal

    getValueAs[Int](key)
  }


  def getLongOrElse(key: String, defaultVal: Long) : Long =
  {
    if (!containsKey(key))
      return defaultVal

    getValueAs[Long](key)
  }


  def getDoubleOrElse(key: String, defaultVal: Double) : Double =
  {
    if (!containsKey(key))
      return defaultVal

    getValueAs[Double](key)
  }


  def getDateOrElse(key: String, defaultVal: DateTime) : DateTime =
  {
    if (!containsKey(key))
      return defaultVal

    getObjectAs[DateTime](key)
  }


  def getValueAs[T](key: String): T =
  {
    getValue(key).asInstanceOf[T]
  }


  def getObjectAs[T >: Null](key: String): T =
  {
    getObject(key).asInstanceOf[T]
  }


  def getValue(key: String): AnyVal


  def getObject(key: String): AnyRef


  def contains(key:String): Boolean = containsKey(key)


  /// <summary>
  /// Convenience method for checking if config key exists.
  /// </summary>
  /// <param name="key"></param>
  /// <returns></returns>
  def containsKey(key: String): Boolean


  def size(): Int
}
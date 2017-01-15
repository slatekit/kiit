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
package slate.common.serialization

import slate.common.{DateTime, Strings}

class ObjectBuilderJson(indentEnabled:Boolean, indent:String)
  extends ObjectBuilder(indentEnabled, indent) {

  private var _propCount = 0

  override def begin():Unit = {
    _buffer.append("{ ")
  }

  /**
    * puts a boolean key/value pair
    * @param key
    * @param value
    */
  def putBoolean(key:String, value:Boolean): Unit ={
    putStringRaw(key, value.toString().toLowerCase())
  }


  /**
    * puts a integer key/value pair
    * @param key
    * @param value
    */
  def putInt(key:String, value:Int): Unit ={
    putStringRaw(key, value.toString())
  }


  /**
    * puts a long key/value pair
    * @param key
    * @param value
    */
  def putNumberLong(key:String, value:Long): Unit ={
    putStringRaw(key, value.toString())
  }


  /**
    * puts a double key/value pair
    * @param key
    * @param value
    */
  def putDouble(key:String, value:Double): Unit ={
    putStringRaw(key, value.toString())
  }


  /**
    *
    * @param key
    * @param value
    */
  def putDateTime(key:String, value:DateTime): Unit ={
    putStringRaw(key, "\"" + value.toStringMySql() + "\"")
  }


  override def putString(key:String, value:String) :Unit = {

    val finalValue = if (value == null) {
      "null"
    }
    else if (Strings.isNullOrEmpty(value)) {
      "\"" + "\""
    }
    else {
      "\"" + value.replaceAllLiterally("\"", "\\\"") + "\""
    }
    putStringRaw(key, finalValue)
  }


  def putStringRaw(key:String, value:String): Unit = {
    val comma = if (_propCount > 0) ", " else " "
    _buffer.append(comma)
    if(indentEnabled){
      _buffer.append(Strings.newline())
      _buffer.append(_indent)
    }

    _buffer.append("\"" + key + "\" : " + value)
    _propCount = _propCount + 1
  }


  override def end():Unit = {
    _buffer.append(" }")
  }
}

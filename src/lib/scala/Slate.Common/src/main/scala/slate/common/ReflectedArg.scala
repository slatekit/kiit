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

import scala.reflect.runtime.universe.{Type,TypeSymbol}

case class ReflectedArg(name:String, typeName:String, pos:Int, sym:AnyRef,
                        isParamDefaulted:Boolean = false) {

  def asType():Type = {
    sym.asInstanceOf[TypeSymbol].toType
  }


  def isBasicType():Boolean = {

    val paramType = typeName

    // Convert to appropriate type
    if(paramType == "String")
    {
      return true
    }
    else if(paramType == "Int")
    {
      return true
    }
    else if(paramType == "Boolean")
    {
      return true
    }
    else if(paramType == "Long")
    {
      return true
    }
    else if(paramType == "Double")
    {
      return true
    }
    false
  }


  def sample():String = {

    val paramType = typeName

    // Convert to appropriate type
    if(paramType == "String")
    {
      return "\"text\""
    }
    else if(paramType == "Int")
    {
      return "1"
    }
    else if(paramType == "Boolean")
    {
      return "true"
    }
    else if(paramType == "Long")
    {
      return "10"
    }
    else if(paramType == "Double")
    {
      return "3.14"
    }
    else if(paramType == "DateTime") {
      return DateTime.now().toStringYYYYMMDDHHmmss()
    }
    "??"
  }
}

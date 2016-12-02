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

    typeName match {
      case "String"  => true
      case "Int"     => true
      case "Boolean" => true
      case "Long"    => true
      case "Double"  => true
      case _         => false
    }
  }


  def sample():String = {

    typeName match
    {
      case "String"   => "\"text\""
      case "Int"      => "1"
      case "Boolean"  => "true"
      case "Long"     => "10"
      case "Double"   => "3.14"
      case "DateTime" => DateTime.now().toStringYYYYMMDDHHmmss()
      case _ => "??"
    }
  }
}

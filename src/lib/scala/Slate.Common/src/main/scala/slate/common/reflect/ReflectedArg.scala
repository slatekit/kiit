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
package slate.common.reflect

import slate.common.DateTime

import scala.reflect.runtime.universe.{Type, TypeSymbol}


/**
 * Used to wrap a type for inspection
 * @param name
 * @param typeName
 * @param pos
 * @param sym
 * @param isParamDefaulted
 */
case class ReflectedArg(name:String,
                        typeName:String,
                        pos:Int,
                        sym:scala.reflect.runtime.universe.Symbol,
                        tpe:scala.reflect.runtime.universe.Type,
                        isParamDefaulted:Boolean = false) {

  def asType():Type = sym.asInstanceOf[TypeSymbol].toType


  def isBasicType():Boolean = {

    typeName match {
      case "String"  => true
      case "Boolean" => true
      case "Int"     => true
      case "Long"    => true
      case "Float"   => true
      case "Double"  => true
      case _         => false
    }
  }


  def sample():String = {

    typeName match
    {
      case "String"   => "\"text\""
      case "Boolean"  => "true"
      case "Int"      => "1"
      case "Long"     => "10"
      case "Float"    => "3.1"
      case "Double"   => "3.14"
      case "DateTime" => DateTime.now().toStringYYYYMMDDHHmmss()
      case _ => "??"
    }
  }
}

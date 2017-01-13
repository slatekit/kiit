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

package slate.common.databases

import slate.common.{Reflector, DateTime, Strings}
import scala.reflect.runtime.universe.{Type, typeOf}

/**
  * Internal USE ONLY
  */
class DbField
{
  var name       = ""
  var dataType   = ""
  var nullable   = ""
  var key        = ""
  var defaultVal = ""
  var extra      = ""

  def isNull :Boolean = !Strings.isMatch(nullable, "NO")


  def isKey  :Boolean = Strings.isMatch(key, "PRI")


  def getFieldType():Type = {

    dataType match {
      case "int(11)"      => typeOf[Int]
      case "int(15)"      => typeOf[Long]
      case "int(6)"       => typeOf[Int]
      case "tinyint(1)"   => typeOf[Short]
      case "bit(1)"       => typeOf[Boolean]
      case "datetime"     => typeOf[DateTime]
      case "longtext"     => Reflector.getFieldTypeString()
      case s if(isVar(s)) => Reflector.getFieldTypeString()
      case _              => typeOf[String]
    }
  }


  def maxLength():Int = {
    dataType match {
      case "longtext"     => -1
      case s if(isVar(s)) => lengthFromVar(s)
      case _              => -1
    }
  }


  def isVar(s:String): Boolean = s.startsWith("varchar")


  def lengthFromVar(s:String):Int = {
      s.replaceAllLiterally(")", "").replaceAllLiterally("varchar(", "").toInt
  }


  override def toString(): String = {
    name +
    ", " + dataType   +
    ", " + nullable   +
    ", " + key        +
    ", " + defaultVal +
    ", " + extra
  }
}

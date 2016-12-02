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

  def isNull     = {
    !Strings.isMatch(nullable, "NO")
  }


  def isKey = {
    Strings.isMatch(key, "PRI")
  }


  def getFieldType():Type = {

    if (dataType == "int(11)") return typeOf[Int]
    if (dataType == "int(15)") return typeOf[Long]
    if (dataType == "int(6)")  return typeOf[Int]
    if (dataType == "tinyint(1)") return typeOf[Short]
    if (dataType == "bit(1)") return typeOf[Boolean]
    if (dataType == "datetime") return typeOf[DateTime]
    if (dataType == "longtext") return Reflector.getFieldTypeString()
    if (dataType.startsWith("varchar")) return Reflector.getFieldTypeString()

    typeOf[String]
  }


  def maxLength():Int = {
    if (dataType == "longtext") {
      return -1
    }
    if (dataType.startsWith("varchar"))
    {
      val len = dataType.replaceAllLiterally(")", "").replaceAllLiterally("varchar(", "").toInt
      return len
    }
    -1
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

/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.databases

import scala.reflect.runtime.universe._
import slate.common.{TimeSpan, DateTime}


object DbConstants {

  val DbMySql = "mysql"
  val DbSqlServer = "sqlserver"

  val EmptyString = "''"
  val TypeNumber = 0
  val TypeNumberShort = 1
  val TypeNumberLong = 2
  val TypeString = 10
  val TypeBool = 11
  val TypeReal = 12
  val TypeDate = 20
  val TypeTime = 21
  val TypeEnum = 22



  def getNameForSqlLite(sqlType:Int):String =
  {
    sqlType match {
      case TypeNumberShort => "INTEGER"
      case TypeNumber      => "INTEGER"
      case TypeNumberLong  => "INTEGER"
      case TypeString      => "TEXT"
      case TypeBool        => "INTEGER"
      case TypeReal        => "REAL"
      case TypeDate        => "INTEGER"
      case TypeTime        => "INTEGER"
      case _               => "INTEGER"
    }
  }


  def getName(sqlType:Int):String =
  {
    sqlType match {
      case TypeNumberShort => "TINYINT"
      case TypeNumber      => "INTEGER"
      case TypeNumberLong  => "BIGINT"
      case TypeString      => "NVARCHAR"
      case TypeBool        => "BIT"
      case TypeReal        => "REAL"
      case TypeDate        => "DATETIME"
      case TypeTime        => "INTEGER"
      case _               => "INTEGER"
    }
  }


  def getTypeFromScala(dataType:Type):Int =
  {
    if (dataType == typeOf[Boolean])       TypeBool
    else if (dataType == typeOf[DateTime]) TypeDate
    else if (dataType == typeOf[Int])      TypeNumber
    else if (dataType == typeOf[Short])    TypeNumberShort
    else if (dataType == typeOf[Long])     TypeNumberLong
    else if (dataType == typeOf[Double])   TypeReal
    else if (dataType == typeOf[String])   TypeString
    else if (dataType == typeOf[TimeSpan]) TypeTime
    else TypeString
  }

}

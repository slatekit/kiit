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
package slate.common.databases

import scala.reflect.runtime.universe.{Type, typeOf}
import slate.common.{TimeSpan, DateTime}

object DbFuncs {



  def getNameForSqlLite(sqlType:DbFieldType):String =
  {
    sqlType match {
      case DbFieldTypeNumber      => "INTEGER"
      case DbFieldTypeShort       => "INTEGER"
      case DbFieldTypeLong        => "INTEGER"
      case DbFieldTypeString      => "TEXT"
      case DbFieldTypeBool        => "INTEGER"
      case DbFieldTypeReal        => "REAL"
      case DbFieldTypeDate        => "INTEGER"
      case DbFieldTypeTime        => "INTEGER"
      case _               => "INTEGER"
    }
  }


  def getName(sqlType:DbFieldType):String =
  {
    sqlType match {
      case DbFieldTypeNumber      => "INTEGER"
      case DbFieldTypeShort       => "TINYINT"
      case DbFieldTypeLong        => "BIGINT"
      case DbFieldTypeString      => "NVARCHAR"
      case DbFieldTypeBool        => "BIT"
      case DbFieldTypeReal        => "REAL"
      case DbFieldTypeDate        => "DATETIME"
      case DbFieldTypeTime        => "INTEGER"
      case _                      => "INTEGER"
    }
  }


  def getTypeFromScala(dataType:Type):DbFieldType =
  {
    if (dataType == typeOf[Boolean])       DbFieldTypeBool
    else if (dataType == typeOf[DateTime]) DbFieldTypeDate
    else if (dataType == typeOf[Int])      DbFieldTypeNumber
    else if (dataType == typeOf[Short])    DbFieldTypeShort
    else if (dataType == typeOf[Long])     DbFieldTypeLong
    else if (dataType == typeOf[Double])   DbFieldTypeReal
    else if (dataType == typeOf[String])   DbFieldTypeString
    else if (dataType == typeOf[TimeSpan]) DbFieldTypeTime
    else DbFieldTypeString
  }
}

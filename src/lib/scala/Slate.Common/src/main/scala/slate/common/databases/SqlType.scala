package slate.common.databases

import slate.common.{DateTime, TimeSpan}

import scala.reflect.runtime.universe._


object SqlType {

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
    
    if (dataType == typeOf[Boolean])  return TypeBool
    if (dataType == typeOf[DateTime]) return TypeDate
    if (dataType == typeOf[Int])      return TypeNumber
    if (dataType == typeOf[Short])    return TypeNumberShort
    if (dataType == typeOf[Long])     return TypeNumberLong
    if (dataType == typeOf[Double])   return TypeReal
    if (dataType == typeOf[String])   return TypeString
    if (dataType == typeOf[TimeSpan]) return TypeTime
    //if (dataType.IsEnum) return SqlType.TypeNumber
    SqlType.TypeString
  }

}

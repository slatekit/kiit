package slate.common

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
    
    if (dataType == typeOf[Boolean])  return SqlType.TypeBool
    if (dataType == typeOf[DateTime]) return SqlType.TypeDate
    if (dataType == typeOf[Int])      return SqlType.TypeNumber
    if (dataType == typeOf[Short])    return SqlType.TypeNumberShort
    if (dataType == typeOf[Long])     return SqlType.TypeNumberLong
    if (dataType == typeOf[Double])   return SqlType.TypeReal
    if (dataType == typeOf[String])   return SqlType.TypeString
    if (dataType == typeOf[TimeSpan]) return SqlType.TypeTime
    //if (dataType.IsEnum) return SqlType.TypeNumber
    SqlType.TypeString
  }

}

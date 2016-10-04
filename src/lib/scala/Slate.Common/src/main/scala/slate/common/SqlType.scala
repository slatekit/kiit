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
    if( sqlType == TypeNumberShort)
      return "INTEGER"

    if( sqlType == TypeNumber)
      return "INTEGER"

    if( sqlType == TypeNumberLong)
      return "INTEGER"

    if( sqlType == TypeString)
      return "TEXT"

    if( sqlType == TypeBool)
      return "INTEGER"

    if( sqlType == TypeReal)
      return "REAL"

    if( sqlType == TypeDate)
      return "INTEGER"

    if( sqlType == TypeTime)
      return "INTEGER"

    return "INTEGER"
  }


  def getName(sqlType:Int):String =
  {
    if( sqlType == TypeNumberShort)
      return "TINYINT"

    if( sqlType == TypeNumber)
      return "INTEGER"

    if( sqlType == TypeNumberLong)
      return "BIGINT"

    if( sqlType == TypeString)
      return "NVARCHAR"

    if( sqlType == TypeBool)
      return "BIT"

    if( sqlType == TypeReal)
      return "REAL"

    if( sqlType == TypeDate)
      return "DATETIME"

    if( sqlType == TypeTime)
      return "INTEGER"

    return "INTEGER"
  }


  def getTypeFromScala(dataType:Type):Int =
  {
    if (dataType == typeOf[Boolean]) return SqlType.TypeBool
    if (dataType == typeOf[DateTime]) return SqlType.TypeDate
    if (dataType == typeOf[Int]) return SqlType.TypeNumber
    if (dataType == typeOf[Short]) return SqlType.TypeNumberShort
    if (dataType == typeOf[Long]) return SqlType.TypeNumberLong
    if (dataType == typeOf[Double]) return SqlType.TypeReal
    if (dataType == typeOf[String]) return SqlType.TypeString
    if (dataType == typeOf[TimeSpan]) return SqlType.TypeTime
    //if (dataType.IsEnum) return SqlType.TypeNumber
    SqlType.TypeString
  }

}

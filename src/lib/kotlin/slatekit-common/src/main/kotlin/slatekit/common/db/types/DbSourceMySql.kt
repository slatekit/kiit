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

package slatekit.common.db.types


import slatekit.common.db.*
import slatekit.common.db.DbUtils.ensureField
import slatekit.common.newline


/**
  * Builds up database tables, indexes and other database components
  */
open class DbSourceMySql : DbSource {

  /**
   * Builds the drop table DDL for the name supplied.
   */
  override fun buildDropTable(name:String): String
  {
    val tableName = ensureField(name)
    val sql = "DROP TABLE IF EXISTS $tableName;"
    return sql
  }


  /**
   * Builds a delete statement to delete all rows
   */
  override fun buildDeleteAll(name: String): String {

    val tableName = ensureField(name)
    val sql = "DELETE * FROM $tableName IF EXISTS $tableName;"
    return sql
  }


  /**
   * Builds an add column DDL sql statement
   */
  override fun buildAddCol(name:String, dataType: DbFieldType, required:Boolean, maxLen:Int): String
  {
    val nullText = if(required ) "NOT NULL" else ""
    val colType = buildColType(dataType, maxLen)
    val colName = buildColName(name)

    val sql = " $newline$colName $colType $nullText"
    return sql
  }


  /**
   * Builds a valid column name
   */
  override fun buildColName(name:String): String = "`" + ensureField(name) + "`"


  /**
   * Builds a valid column type
   */
  override fun buildColType(colType: DbFieldType, maxLen:Int): String
  {
    return if(colType == DbFieldTypeString && maxLen == -1)
      "longtext"
    else if(colType == DbFieldTypeString)
      "VARCHAR(" + maxLen + ")"
    else
      getColTypeName(colType)
  }



  protected fun getColTypeName(sqlType:DbFieldType):String {
    return when (sqlType) {
      DbFieldTypeString        -> "NVARCHAR"
      DbFieldTypeBool          -> "BIT"
      DbFieldTypeShort         -> "TINYINT"
      DbFieldTypeNumber        -> "INTEGER"
      DbFieldTypeLong          -> "BIGINT"
      DbFieldTypeReal          -> "REAL"
      DbFieldTypeLocalDate     -> "DATE"
      DbFieldTypeLocalTime     -> "TIME"
      DbFieldTypeLocalDateTime -> "DATETIME"
      DbFieldTypeZonedDateTime -> "DATETIME"
      DbFieldTypeInstant       -> "INSTANT"
      DbFieldTypeDateTime      -> "DATETIME"
      else                     -> "INTEGER"
    }
  }
}

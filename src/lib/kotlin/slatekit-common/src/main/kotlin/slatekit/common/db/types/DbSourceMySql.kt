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


import slatekit.common.Model
import slatekit.common.Strings
import slatekit.common.db.*
import slatekit.common.db.DbFuncs.ensureField
import slatekit.common.db.DbFuncs.getTypeFromLang



/**
  * Builds up database tables, indexes and other database components
  */
open class DbSourceMySql : DbSource {

  /**
   * Builds the table DDL sql statement using the model supplied.
   */
  override fun builAddTable(model: Model): String
  {
    val buff = StringBuilder()

    // 1. build the "CREATE <tablename>
    buff.append( buildCreateTable(model.name) )

    // 2. build the primary key column
    buff.append(buildPrimaryKey("id"))

    // 3. Now build all the columns
    // Get only fields ( excluding primary key )
    val dataFields = model.fields.filter{ "id".compareTo(it.name) != 0 }

    // Build sql for the data fields.
    val dataFieldSql = dataFields.fold("", { acc, field ->
      val sqlType = getTypeFromLang(field.dataType)
      acc + ", " + this.buildAddCol(field.name, sqlType, field.isRequired, field.maxLength)
    })
    buff.append( dataFieldSql )

    // 4. finish the construction and get the sql.
    buff.append(" );")
    val sql = buff.toString()
    return sql
  }


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
   * Builds an add column DDL sql statement
   */
  override fun buildAddCol(name:String, dataType: DbFieldType, required:Boolean, maxLen:Int): String
  {
    val nullText = if(required ) "NOT NULL" else ""
    val colType = buildColType(dataType, maxLen)
    val colName = buildColName(name)

    val sql = " " + Strings.newline() + colName + " " + colType + " " + nullText
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
      DbFieldTypeString -> "NVARCHAR"
      DbFieldTypeBool   -> "BIT"
      DbFieldTypeShort  -> "TINYINT"
      DbFieldTypeNumber -> "INTEGER"
      DbFieldTypeLong   -> "BIGINT"
      DbFieldTypeReal   -> "REAL"
      DbFieldTypeDate   -> "DATETIME"
      DbFieldTypeTime   -> "INTEGER"
      else              -> "INTEGER"
    }
  }


  protected fun buildPrimaryKey(name:String): String
  {
    val finalName = ensureField(name)
    return "`$finalName` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY"
  }


  protected fun buildCreateTable(name:String): String =
    "create table `" + name + "` ( " + Strings.newline()

}

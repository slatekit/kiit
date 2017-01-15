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


import slate.common.{ModelField, Strings, Model}
import slate.common.databases.DbConstants._
import slate.common.query.{QueryEncoder}


/**
  * Builds up database tables, indexes and other database components
  */
class DbBuilder() {


  def addTable(model:Model): String =
  {
    val buff = new StringBuilder()

    // 1. build the "CREATE <tablename>
    buff.append( getCreateTable(model.name) )

    // 2. build the primary key column
    buff.append(getPrimaryKey("id") + ",")

    // 3. Now build all the columns
    // Get only fields ( excluding primary key )
    val dataFields = model.fields.filter( field => !Strings.isMatch(field.name, "id"))

    // Build sql for the data fields.
    val dataFieldSql = Strings.mkString[ModelField]( dataFields, field => {
      val sqlType = getTypeFromScala(field.dataType)
      this.addColNew(field.name, sqlType, field.isRequired, field.maxLength)
    }, ",")
    buff.append( dataFieldSql )

    // 4. finish the construction and get the sql.
    buff.append(" );")
    val sql = buff.toString()
    sql
  }


  def dropTable(name:String): String =
  {
    val tableName = QueryEncoder.ensureField(name)
    val sql = s"DROP TABLE IF EXISTS $tableName;"
    sql
  }


  def addCol(table:String, col:String, sqlType:Int): DbBuilder =
  {
    this
  }


  def addColNew(name:String, dataType:Int, required:Boolean = false, maxLen:Int = 0): String =
  {
    val nullText = if(required ) "NOT NULL" else ""
    val colType = getColType(dataType, maxLen)
    val colName = getColName(name)

    val sql = " " + Strings.newline() + colName + " " + colType + " " + nullText
    sql
  }


  def getColName(name:String): String =
  {
    if (Strings.compare(name, "key", true) == 0)
      "`" + name + "`"
    else if (Strings.compare(name, "group", true) == 0)
      "`" + name + "`"
    else
      name + " "
  }


  def getColType(colType:Int, maxLen:Int): String =
  {
    if(colType == TypeString && maxLen == -1)
      "longtext"
    else if(colType == TypeString)
      "VARCHAR(" + maxLen + ")"
    else
      getName(colType)
  }


  protected def getPrimaryKey(name:String): String =
  {
    val finalName = QueryEncoder.ensureField(name)
    s"`$finalName` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY"
  }


  protected def getCreateTable(name:String): String =
  {
    "create table `" + name + "` ( " + Strings.newline()
  }
}

package slatekit.meta

import slatekit.common.LowerCamel
import slatekit.common.Namer
import slatekit.common.db.Db
import slatekit.common.db.DbFieldTypeNumber
import slatekit.common.db.DbUtils
import slatekit.common.db.types.DbSource
import slatekit.common.newline
import slatekit.meta.models.Model




/**
 * creates a table in the database that matches the schema(fields) in the model supplied
 *
 * @param model : The model associated with the table.
 */
fun createTable(db: Db, model: Model): Unit {
    val dbSrc = db.source
    val sql = buildAddTable(dbSrc, model)
    db.execute(sql)
}


/**
 * Builds the table DDL sql statement using the model supplied.
 */
fun buildAddTable(dbSrc:DbSource, model: Model, namer: Namer? = null): String
{
    val buff = StringBuilder()

    // 1. build the "CREATE <tablename>
    buff.append(buildCreateTable(namer?.rename(model.name) ?: model.name) )

    // 2. build the primary key column
    buff.append(buildPrimaryKey("id"))

    // 3. Now build all the columns
    // Get only fields ( excluding primary key )
    val dataFields = model.fields.filter{ "id".compareTo(it.name) != 0 }

    // Build sql for the data fields.
    val dataFieldSql = dataFields.fold("", { acc, field ->
        val sqlType = if(field.isEnum) {
            DbFieldTypeNumber
        } else {
            DbUtils.getTypeFromLang(field.dataType.java)
        }
        acc + ", " + dbSrc.buildAddCol(field.storedName, sqlType, field.isRequired, field.maxLength)
    })
    buff.append( dataFieldSql )

    // 4. finish the construction and get the sql.
    buff.append(" );")
    val sql = buff.toString()
    return sql
}


fun buildPrimaryKey(name:String): String
{
    val finalName = DbUtils.ensureField(name)
    return "`$finalName` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY"
}


fun buildCreateTable(name:String): String =
        "create table `" + name + "` ( " + newline

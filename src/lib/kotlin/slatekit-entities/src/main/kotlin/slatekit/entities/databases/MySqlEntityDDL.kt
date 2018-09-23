package slatekit.entities.databases

import slatekit.common.Namer
import slatekit.common.db.Db
import slatekit.common.db.DbFieldTypeNumber
import slatekit.common.db.DbUtils
import slatekit.common.db.types.DbSource
import slatekit.common.newline
import slatekit.entities.core.EntityDDL
import slatekit.meta.models.Model

class MySqlEntityDDL : EntityDDL {

    /**
     * creates a table in the database that matches the schema(fields) in the model supplied
     *
     * @param model : The model associated with the table.
     */
    override fun createTable(db: Db, model: Model) {
        val dbSrc = db.source
        val sql = buildAddTable(dbSrc, model)
        db.execute(sql)
    }


    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    override fun buildAddTable(dbSrc: DbSource, model: Model, namer: Namer?): String
    {
        val buff = StringBuilder()

        // 1. build the "CREATE <tablename>
        buff.append(buildCreateTable(namer?.rename(model.name) ?: model.name) )

        // 2. build the primary key column
        buff.append(buildPrimaryKey("id"))

        // 3. Now build all the columns
        // Get only fields ( excluding primary key )
        // Build sql for the data fields.
        val dataFieldSql = buildColumns(null, dbSrc, model, true, namer)
        buff.append( dataFieldSql )

        // 4. finish the construction and get the sql.
        buff.append(" );")
        val sql = buff.toString()
        return sql
    }


    override fun buildPrimaryKey(name:String): String
    {
        val finalName = DbUtils.ensureField(name)
        return "`$finalName` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY"
    }


    override fun buildCreateTable(name:String): String = "create table `" + name + "` ( " + newline


    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    fun buildColumns(prefix:String?, dbSrc: DbSource, model: Model, filterId:Boolean, namer: Namer?): String
    {
        val buff = StringBuilder()

        // 3. Now build all the columns
        // Get only fields ( excluding primary key )
        val dataFields = if(filterId) model.fields.filter{ "id".compareTo(it.name) != 0 } else model.fields

        // Build sql for the data fields.
        val dataFieldSql = dataFields.fold("", { acc, field ->
            val finalStoredName = prefix?.let { prefix + "_" + field.storedName } ?: field.storedName
            if(field.isEnum) {
                acc + ", " + dbSrc.buildAddCol(finalStoredName, DbFieldTypeNumber, field.isRequired, field.maxLength)
            } else if(field.model != null) {
                val sql = buildColumns(field.storedName, dbSrc, field.model!!, false, namer)
                acc + sql
            }
            else {
                val sqlType = DbUtils.getTypeFromLang(field.dataCls.java)
                acc + ", " + dbSrc.buildAddCol(finalStoredName, sqlType, field.isRequired, field.maxLength)
            }
        })
        buff.append( dataFieldSql )

        val sql = buff.toString()
        return sql
    }
}
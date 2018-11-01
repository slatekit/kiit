package slatekit.entities.databases

import slatekit.common.naming.Namer
import slatekit.common.db.Db
import slatekit.common.db.DbFieldType
import slatekit.common.db.DbUtils
import slatekit.common.newline
import slatekit.meta.models.Model

/**
 *
 * 1. CREATE INDEX idx_lastname ON message (status);
 * 2. ALTER TABLE message DROP INDEX idx_status;
 * 3. ALTER TABLE message ADD UNIQUE (uuid);
 */
open class SqlBuilder(val types:TypeMap, val namer: Namer?) {

    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    fun createTable(model:Model): String
    {
        val buff = StringBuilder()

        // 1. build the "CREATE <tablename>
        buff.append(createTableName(model))

        // 2. build the primary key column
        buff.append(createKey("id"))

        // 3. Now build all the columns
        // Get only fields ( excluding primary key )
        // Build sql for the data fields.
        val dataFieldSql = createColumns(null, model, true)
        buff.append(dataFieldSql)

        // 4. finish the construction and get the sql.
        buff.append(" );")
        val sql = buff.toString()
        return sql
    }


    fun createTableName(model:Model): String {
        val name = namer?.rename(model.name) ?: model.name
        return "create table `$name` ( $newline"
    }


    fun createKey(name: String): String
    {
        val finalName = DbUtils.ensureField(name)
        return "`$finalName` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY"
    }


    fun createIndex(db: Db, model: Model, namer: Namer?): List<String> {
        val dbSrc = db.source
        val tableName = namer?.rename(model.name) ?: model.name
        val indexes = model.fields.filter { it.isIndexed }
        val indexSql = indexes.map { field ->
            "CREATE INDEX idx_${field.storedName} ON $tableName (${field.storedName});"
        }
        // db.execute(indexSql)

        val uniques = model.fields.filter { it.isUnique }
        val uniqueSql = uniques.map { field ->
            "ALTER TABLE $tableName ADD UNIQUE (${field.storedName});"
        }
        return indexSql.plus(uniqueSql)
    }


    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    fun createColumns(prefix: String?, model: Model, filterId: Boolean): String
    {
        val buff = StringBuilder()

        // 3. Now build all the columns
        // Get only fields ( excluding primary key )
        val dataFields = if (filterId) model.fields.filter { "id".compareTo(it.name) != 0 } else model.fields

        // Build sql for the data fields.
        val dataFieldSql = dataFields.fold("", { acc, field ->
            val finalStoredName = prefix?.let { prefix + "_" + field.storedName } ?: field.storedName
            if (field.isEnum) {
                acc + ", " + createCol(finalStoredName, DbFieldType.DbNumber, field.isRequired, field.maxLength)
            } else if (field.model != null) {
                val sql = createColumns(field.storedName, field.model!!, false)
                acc + sql
            } else {
                val sqlType = DbUtils.getTypeFromLang(field.dataCls.java)
                acc + ", " + createCol(finalStoredName, sqlType, field.isRequired, field.maxLength)
            }
        })
        buff.append(dataFieldSql)

        val sql = buff.toString()
        return sql
    }


    fun createCol(name: String, dataType: DbFieldType, required: Boolean, maxLen: Int): String {
        val nullText = if (required) "NOT NULL" else ""
        val colType = colType(dataType, maxLen)
        val colName = colName(name)

        val sql = " $newline$colName $colType $nullText"
        return sql
    }


    /**
     * Builds a valid column name
     */
    fun colName(name: String): String = "`" + DbUtils.ensureField(name) + "`"


    /**
     * Builds a valid column type
     */
    fun colType(colType: DbFieldType, maxLen: Int): String {
        return if (colType == DbFieldType.DbText && maxLen == -1)
            types.textType.dbType
        else if (colType == DbFieldType.DbString)
            types.stringType.dbType + "($maxLen)"
        else
            types.lookup[colType]?.dbType ?: ""

    }
}

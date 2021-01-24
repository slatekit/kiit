package slatekit.migrations


import slatekit.common.naming.Namer
import slatekit.common.data.DataType
import slatekit.common.data.Encoding
import slatekit.common.newline
import slatekit.data.core.Types
import slatekit.meta.models.Model

/**
 *
 * 1. CREATE INDEX idx_lastname ON message (status);
 * 2. ALTER TABLE message DROP INDEX idx_status;
 * 3. ALTER TABLE message ADD UNIQUE (uuid);
 */
open class SqlBuilder(val types: Types, val namer: Namer?) {
    private val defaultID = "id"

    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    fun createTable(model:Model): String
    {
        val buff = StringBuilder()

        // 1. build the "CREATE <tablename>
        buff.append(createTableName(model))

        // 2. build the primary key column
        val idCol = model.idField?.storedName ?: defaultID
        buff.append(createKey(idCol))

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
        val name = namer?.rename(model.table) ?: model.table
        return "create table `$name` ( $newline"
    }


    fun createKey(name: String): String
    {
        val finalName = Encoding.ensureField(name)
        return "`$finalName` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY"
    }


    fun createIndex(model: Model): List<String> {
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
        val idCol = model.idField?.storedName ?: defaultID

        // 3. Now build all the columns
        // Get only fields ( excluding primary key )
        val dataFields = if (filterId) model.fields.filter { idCol.compareTo(it.name) != 0 } else model.fields

        // Build sql for the data fields.
        val dataFieldSql = dataFields.fold("") { acc, field ->
            val finalStoredName = prefix?.let { prefix + "_" + field.storedName } ?: field.storedName
            if (field.isEnum) {
                acc + ", " + createCol(finalStoredName, DataType.DbNumber, field.isRequired, field.maxLength)
            } else if (field.model != null) {
                val sql = field.model?.let { createColumns(field.storedName, it, false) }
                acc + sql
            } else {
                val sqlType = DataType.getTypeFromLang(field.dataCls.java)
                acc + ", " + createCol(finalStoredName, sqlType, field.isRequired, field.maxLength)
            }
        }
        buff.append(dataFieldSql)

        val sql = buff.toString()
        return sql
    }


    fun createCol(name: String, dataType: DataType, required: Boolean, maxLen: Int): String {
        val nullText = if (required) "NOT NULL" else ""
        val colType = colType(dataType, maxLen)
        val colName = colName(name)

        val sql = " $newline$colName $colType $nullText"
        return sql
    }


    /**
     * Builds a valid column name
     */
    fun colName(name: String): String = "`" + Encoding.ensureField(name) + "`"


    /**
     * Builds a valid column type
     */
    fun colType(colType: DataType, maxLen: Int): String {
        return if (colType == DataType.DbText && maxLen == -1)
            types.textType.dbType
        else if (colType == DataType.DbString && maxLen == -1)
            types.textType.dbType
        else if (colType == DataType.DbString)
            types.stringType.dbType + "($maxLen)"
        else
            types.lookup[colType]?.dbType ?: ""

    }
}

package slatekit.migrations

import slatekit.common.data.DataType
import slatekit.common.naming.Namer
import slatekit.common.newline
import slatekit.data.sql.Dialect
import slatekit.meta.models.Model

/**
 * 1. CREATE INDEX idx_lastname ON message (status);
 * 2. ALTER TABLE message DROP INDEX idx_status;
 * 3. ALTER TABLE message ADD UNIQUE (uuid);
 */
open class SqlBuilderDDL(override val dialect: Dialect, val namer: Namer?) : SqlBuilder {
    private val defaultID = "id"
    private val types = dialect.types

    /**
     * Builds the create table DDL for this model
     */
    override fun create(model: Model): String
    {
        val buff = StringBuilder()

        // 1. build the "CREATE <tablename>
        val tableName = dialect.encode(model.table)
        buff.append("create table $tableName ( $newline")

        // 2. build the primary key column
        val idCol = model.idField?.storedName ?: defaultID
        buff.append(createPrimaryKey(idCol))

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


    override fun remove(model: Model): String {
        val tableName = dialect.encode(model.table)
        return "drop table if exists $tableName;"
    }


    override fun clear(model: Model): String {
        val tableName = dialect.encode(model.table)
        return "truncate table $tableName;"
    }


    override fun createPrimaryKey(name: String): String
    {
        val finalName = dialect.encode(name)
        return "$finalName BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY"
    }


    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    override fun createColumns(prefix: String?, model: Model, filterId: Boolean): String
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
                acc + ", " + createColumn(finalStoredName, DataType.DTInt, field.isRequired, field.maxLength)
            } else if (field.model != null) {
                val sql = field.model?.let { createColumns(field.storedName, it, false) }
                acc + sql
            } else {
                val sqlType = DataType.fromJava(field.dataCls.java)
                acc + ", " + createColumn(finalStoredName, sqlType, field.isRequired, field.maxLength)
            }
        }
        buff.append(dataFieldSql)

        val sql = buff.toString()
        return sql
    }


    override fun createIndexes(model: Model): List<String> {
        val tableName = namer?.rename(model.name) ?: model.name
        val indexes = model.fields.filter { it.isIndexed }
        val indexSql = indexes.map { field ->
            "CREATE INDEX idx_${field.storedName} ON $tableName (${field.storedName});"
        }
        val uniques = model.fields.filter { it.isUnique }
        val uniqueSql = uniques.map { field ->
            "ALTER TABLE $tableName ADD UNIQUE (${field.storedName});"
        }
        return indexSql.plus(uniqueSql)
    }


    private fun createColumn(name: String, dataType: DataType, required: Boolean, maxLen: Int): String {
        val nullText = if (required) "NOT NULL" else ""
        val colType = colType(dataType, maxLen)
        val colName = dialect.encode(name)

        val sql = " $newline$colName $colType $nullText"
        return sql
    }


    /**
     * Handle length for Strings
     */
    private fun colType(colType: DataType, maxLen: Int): String {
        return if (colType == DataType.DTText && maxLen == -1)
            types.textType.dbType
        else if (colType == DataType.DTString && maxLen == -1)
            types.textType.dbType
        else if (colType == DataType.DTString)
            types.stringType.dbType + "($maxLen)"
        else if (colType == DataType.DTUUID || colType == DataType.DTULID || colType == DataType.DTUPID)
            types.stringType.dbType + "($maxLen)"
        else
            types.lookup[colType]?.dbType ?: ""

    }
}

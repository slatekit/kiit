package kiit.data.sql.vendors

import kiit.common.data.DataType
import kiit.common.newline
import kiit.data.sql.Dialect
import kiit.meta.models.Model
import kiit.meta.models.ModelField
import kiit.utils.naming.Namer

interface DDLBuilder {
    val namer:Namer?
    val dialect:Dialect
    fun create(model: Model): String
    fun delete(model: Model): String
    fun clear(model: Model) : String
    fun createIndexes(model: Model): List<String>
}

data class SqlDDLColumn(val name:String, val type:String, val required:String, val constraints:String)


data class SqlDDLGroup(val schema:String, val statements:List<String>)


/**
 * 1. CREATE INDEX idx_lastname ON message (status);
 * 2. ALTER TABLE message DROP INDEX idx_status;
 * 3. ALTER TABLE message ADD UNIQUE (uuid);
 */
open class SqlDDLBuilder(override val dialect: Dialect,
                         override val namer: Namer?) : DDLBuilder {
    protected val defaultID = "id"
    protected val types = dialect.types
    protected val MAX_COLUMN_TYPE_LENGTH = 15


    /**
     * Builds the create table DDL for this model
     */
    override fun create(model: Model): String
    {
        val buff = StringBuilder()

        // 1. build the "CREATE <tablename>
        val tableName = dialect.encode(model.schema, model.table)
        buff.append("create table if not exists $tableName ( $newline")

        // 2. build the primary key column
        val col1 = createPrimaryKey(model)

        // 3. Now build all the columns
        // Get only fields ( excluding primary key )
        // Build sql for the data fields.
        val colRest = createColumns(null, model, true)
        val columns = listOf<SqlDDLColumn>(col1).plus(colRest)
        val maxColName = columns.maxBy { it.name.length }.name.length
        val maxColType = columns.maxBy { it.type.length }.type.length
        val maxReqType = columns.maxBy { it.required.length }.type.length
        val dataFieldSql = columns.foldIndexed("" ) { pos, acc, col ->
            val name = col.name.padEnd(maxColName, ' ')
            val type = col.type.padEnd(maxColType, ' ')
            val req  = col.required.padEnd(maxReqType, ' ')
            val ddl = "$name $type $req ${col.constraints}"
            if(pos == 0) acc + ddl else "$acc, ${newline}$ddl"
        }
        buff.append(dataFieldSql)

        // 4. finish the construction and get the sql.
        buff.append(" );")
        buff.append(newline)
        val sql = buff.toString()
        return sql
    }


    override fun delete(model: Model): String {
        val tableName = dialect.encode(model.schema, model.table)
        return "drop table if exists $tableName;"
    }


    override fun clear(model: Model): String {
        val tableName = dialect.encode(model.schema, model.table)
        return "truncate table $tableName;"
    }


    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    protected open fun createColumns(prefix: String?, model: Model, filterId: Boolean): List<SqlDDLColumn>
    {
        val idCol = model.idField?.storedName ?: defaultID

        // 3. Now build all the columns
        // Get only fields ( excluding primary key )
        val dataFields = if (filterId) model.fields.filter { idCol.compareTo(it.name) != 0 } else model.fields

        // Build sql for the data fields.
        val columnsDDL = mutableListOf<SqlDDLColumn>()
        dataFields.map { field ->
            val finalStoredName = prefix?.let { prefix + "_" + field.storedName } ?: field.storedName
            if (field.isEnum) {
                val col = createColumn(field, finalStoredName, DataType.DTInt)
                columnsDDL.add(col)
            } else if (field.model != null) {
                val cols = when(field.model) {
                    null -> listOf()
                    else -> createColumns(field.storedName, field.model!!, false)
                }
                columnsDDL.addAll(cols)
            } else {
                val col = createColumn(field, finalStoredName, null)
                columnsDDL.add(col)
            }
        }
        return columnsDDL
    }


    override fun createIndexes(model: Model): List<String> {
        val tableName = dialect.encode(model.schema ?: "", model.table)
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


    protected open fun createPrimaryKey(model: Model): SqlDDLColumn {
        val idCol = model.idField?.storedName ?: defaultID
        val idColumnName = dialect.encode(idCol)
        return SqlDDLColumn(idColumnName, "BIGINT", "NOT NULL", "AUTO_INCREMENT PRIMARY KEY")
    }


    protected open fun createColumn(field: ModelField, name: String, dataType: DataType?): SqlDDLColumn {
        val required = field.isRequired
        val maxLen = field.maxLength
        val nullText = if (required) "NOT NULL" else ""
        val colType = colType(dataType ?: field.dataTpe, maxLen)
        val colName = dialect.encode(name)
        return SqlDDLColumn(colName, colType, nullText, "")
    }


    /**
     * Handle length for Strings
     */
    protected open fun colType(colType: DataType, maxLen: Int): String {
        return if (colType == DataType.DTText && maxLen == -1)
            types.textType.dbType
        else if (colType == DataType.DTString && maxLen == -1)
            types.textType.dbType
        else if (colType == DataType.DTString)
            types.stringType.dbType + "($maxLen)"
        else if (colType == DataType.DTUUID || colType == DataType.DTULID || colType == DataType.DTUPID)
            types.stringType.dbType + "($maxLen)"
        else if(colType == DataType.DTDouble)
            types.doubleType.dbType
        else
            types.lookup[colType]?.dbType ?: ""
    }


    protected fun getFieldByColumnByLength(model:Model, filterId:Boolean = true): ModelField {
        val idCol = model.idField?.storedName ?: defaultID
        val dataFields = if (filterId) model.fields.filter { idCol.compareTo(it.name) != 0 } else model.fields
        val maxColumnName = dataFields.maxBy { it.storedName.length }
        return maxColumnName
    }
}



/**
 * 1. CREATE INDEX idx_lastname ON message (status);
 * 2. ALTER TABLE message DROP INDEX idx_status;
 * 3. ALTER TABLE message ADD UNIQUE (uuid);
 */
open class PostgresSqlDDLBuilder(dialect: Dialect, namer: Namer?) : SqlDDLBuilder(dialect, namer) {

    override fun createPrimaryKey(model:Model): SqlDDLColumn {
        val idCol = model.idField?.storedName ?: defaultID
        val idColumnName = dialect.encode(idCol)
        return SqlDDLColumn(idColumnName, "BIGSERIAL", "NOT NULL", "PRIMARY KEY")
    }


    /**
     *
     * CREATE UNIQUE INDEX plan_userId_purchaseId ON ONLY "products"."plan" USING btree ("userId", "purchaseId")
     * CREATE INDEX concurrently if not exists purchase_userId_sku ON "products"."purchase"("userId", "sku")
     *
     */
    override fun createIndexes(model: Model): List<String> {
        val tableName = dialect.encode(model.schema, model.table)

        // Example:
        // CREATE INDEX concurrently if not exists purchase_userId_sku
        // ON "products"."purchase"("userId", "sku")
        val indexes = model.fields.filter { it.isIndexed }
        val indexSql = indexes.map { field ->
            val uniqueIndexName = "${model.table}_${field.storedName}"
            val uniqueColumn = dialect.encode(field.storedName)
            "CREATE INDEX $uniqueIndexName ON $tableName (${uniqueColumn});"
        }

        // Example:
        // CREATE UNIQUE INDEX plan_userId_purchaseId
        // ON ONLY "products"."plan" USING btree ("userId", "purchaseId")
        val uniques = model.fields.filter { it.isUnique }
        val uniqueSql = uniques.map { field ->
            val uniqueIndexName = "${model.table}_${field.storedName}"
            val uniqueColumn = dialect.encode(field.storedName)
            "CREATE UNIQUE INDEX $uniqueIndexName ON ONLY $tableName USING btree ($uniqueColumn);"
        }
        val all = indexSql.plus(uniqueSql).plus(newline).joinToString(newline)
        return listOf(all)
    }


    override fun colType(colType: DataType, maxLen: Int): String {
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

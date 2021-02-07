/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.db.builders

import java.rmi.UnexpectedException
import slatekit.common.Types
import slatekit.common.data.DataType
import slatekit.common.data.Encoding
import slatekit.common.newline

/**
 * Builds up database tables, indexes and other database components
 */
open class MySqlBuilder : DbBuilder {

    /**
     * Mapping of normalized types ot postgres type names
     */
    val dataToColumnTypes = mapOf(
        DataType.DTString to "NVARCHAR",
        DataType.DTBool to "BIT",
        DataType.DTShort to "TINYINT",
        DataType.DTInt to "INTEGER",
        DataType.DTLong to "BIGINT",
        DataType.DTFloat to "FLOAT",
        DataType.DTDouble to "DOUBLE",
        DataType.DTDecimal to "DECIMAL",
        DataType.DTLocalDate to "DATE",
        DataType.DTLocalTime to "TIME",
        DataType.DTLocalDateTime to "DATETIME",
        DataType.DTZonedDateTime to "DATETIME",
        DataType.DTInstant to "INSTANT",
        DataType.DTDateTime to "DATETIME"
    )

    /**
     * Builds the drop table DDL for the name supplied.
     */
    override fun dropTable(name: String): String = build(name, "DROP TABLE IF EXISTS")

    /**
     * Builds a delete statement to delete all rows
     */
    override fun truncate(name: String): String = build(name, "DELETE FROM")

    /**
     * Builds an add column DDL sql statement
     */
    override fun addCol(name: String, dataType: DataType, required: Boolean, maxLen: Int): String {
        val nullText = if (required) "NOT NULL" else ""
        val colType = colType(dataType, maxLen)
        val colName = colName(name)

        val sql = " $newline$colName $colType $nullText"
        return sql
    }

    /**
     * Builds a valid column name
     */
    override fun colName(name: String): String = "`" + Encoding.ensureField(name) + "`"

    /**
     * Builds a valid column type
     */
    override fun colType(colType: DataType, maxLen: Int): String {
        return if (colType == DataType.DTString && maxLen == -1)
            "longtext"
        else if (colType == DataType.DTString)
            "VARCHAR($maxLen)"
        else
            getColTypeName(colType)
    }

    private fun build(name: String, prefix: String): String {
        val tableName = Encoding.ensureField(name)
        val sql = "$prefix `$tableName`;"
        return sql
    }

    private fun getColTypeName(sqlType: DataType): String {
        return if (dataToColumnTypes.containsKey(sqlType))
            dataToColumnTypes[sqlType] ?: ""
        else
            throw UnexpectedException("Unexpected db type : $sqlType")
    }
}

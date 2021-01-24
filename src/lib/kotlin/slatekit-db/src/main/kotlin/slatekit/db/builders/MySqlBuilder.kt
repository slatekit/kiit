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
        DataType.DbString to "NVARCHAR",
        DataType.DbBool to "BIT",
        DataType.DbShort to "TINYINT",
        DataType.DbNumber to "INTEGER",
        DataType.DbLong to "BIGINT",
        DataType.DbFloat to "FLOAT",
        DataType.DbDouble to "DOUBLE",
        DataType.DbDecimal to "DECIMAL",
        DataType.DbLocalDate to "DATE",
        DataType.DbLocalTime to "TIME",
        DataType.DbLocalDateTime to "DATETIME",
        DataType.DbZonedDateTime to "DATETIME",
        DataType.DbInstant to "INSTANT",
        DataType.DbDateTime to "DATETIME"
    )

    val langToDataTypes = mapOf(
        Types.JBoolClass to DataType.DbBool,
        Types.JStringClass to DataType.DbString,
        Types.JShortClass to DataType.DbShort,
        Types.JIntClass to DataType.DbNumber,
        Types.JLongClass to DataType.DbLong,
        Types.JFloatClass to DataType.DbFloat,
        Types.JDoubleClass to DataType.DbDouble,
        // Types.JDecimalClass to  DataType.DbDecimal,
        Types.JLocalDateClass to DataType.DbLocalDate,
        Types.JLocalTimeClass to DataType.DbLocalTime,
        Types.JLocalDateTimeClass to DataType.DbLocalDateTime,
        Types.JZonedDateTimeClass to DataType.DbZonedDateTime,
        Types.JInstantClass to DataType.DbInstant,
        Types.JDateTimeClass to DataType.DbDateTime
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
        return if (colType == DataType.DbString && maxLen == -1)
            "longtext"
        else if (colType == DataType.DbString)
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

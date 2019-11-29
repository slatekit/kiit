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
import slatekit.common.db.DbFieldType
import slatekit.common.newline
import slatekit.db.DbUtils.ensureField

/**
 * Builds up database tables, indexes and other database components
 */
open class MySqlBuilder : DbBuilder {

    /**
     * Mapping of normalized types ot postgres type names
     */
    val dataToColumnTypes = mapOf(
        DbFieldType.DbString to "NVARCHAR",
        DbFieldType.DbBool to "BIT",
        DbFieldType.DbShort to "TINYINT",
        DbFieldType.DbNumber to "INTEGER",
        DbFieldType.DbLong to "BIGINT",
        DbFieldType.DbFloat to "FLOAT",
        DbFieldType.DbDouble to "DOUBLE",
        DbFieldType.DbDecimal to "DECIMAL",
        DbFieldType.DbLocalDate to "DATE",
        DbFieldType.DbLocalTime to "TIME",
        DbFieldType.DbLocalDateTime to "DATETIME",
        DbFieldType.DbZonedDateTime to "DATETIME",
        DbFieldType.DbInstant to "INSTANT",
        DbFieldType.DbDateTime to "DATETIME"
    )

    val langToDataTypes = mapOf(
        Types.JBoolClass to DbFieldType.DbBool,
        Types.JStringClass to DbFieldType.DbString,
        Types.JShortClass to DbFieldType.DbShort,
        Types.JIntClass to DbFieldType.DbNumber,
        Types.JLongClass to DbFieldType.DbLong,
        Types.JFloatClass to DbFieldType.DbFloat,
        Types.JDoubleClass to DbFieldType.DbDouble,
        // Types.JDecimalClass to  DbFieldType.DbDecimal,
        Types.JLocalDateClass to DbFieldType.DbLocalDate,
        Types.JLocalTimeClass to DbFieldType.DbLocalTime,
        Types.JLocalDateTimeClass to DbFieldType.DbLocalDateTime,
        Types.JZonedDateTimeClass to DbFieldType.DbZonedDateTime,
        Types.JInstantClass to DbFieldType.DbInstant,
        Types.JDateTimeClass to DbFieldType.DbDateTime
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
    override fun addCol(name: String, dataType: DbFieldType, required: Boolean, maxLen: Int): String {
        val nullText = if (required) "NOT NULL" else ""
        val colType = colType(dataType, maxLen)
        val colName = colName(name)

        val sql = " $newline$colName $colType $nullText"
        return sql
    }

    /**
     * Builds a valid column name
     */
    override fun colName(name: String): String = "`" + ensureField(name) + "`"

    /**
     * Builds a valid column type
     */
    override fun colType(colType: DbFieldType, maxLen: Int): String {
        return if (colType == DbFieldType.DbString && maxLen == -1)
            "longtext"
        else if (colType == DbFieldType.DbString)
            "VARCHAR($maxLen)"
        else
            getColTypeName(colType)
    }

    private fun build(name: String, prefix: String): String {
        val tableName = ensureField(name)
        val sql = "$prefix `$tableName`;"
        return sql
    }

    private fun getColTypeName(sqlType: DbFieldType): String {
        return if (dataToColumnTypes.containsKey(sqlType))
            dataToColumnTypes[sqlType] ?: ""
        else
            throw UnexpectedException("Unexpected db type : $sqlType")
    }
}

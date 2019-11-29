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
import slatekit.common.db.DbType
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
        DbType.DbString to "NVARCHAR",
        DbType.DbBool to "BIT",
        DbType.DbShort to "TINYINT",
        DbType.DbNumber to "INTEGER",
        DbType.DbLong to "BIGINT",
        DbType.DbFloat to "FLOAT",
        DbType.DbDouble to "DOUBLE",
        DbType.DbDecimal to "DECIMAL",
        DbType.DbLocalDate to "DATE",
        DbType.DbLocalTime to "TIME",
        DbType.DbLocalDateTime to "DATETIME",
        DbType.DbZonedDateTime to "DATETIME",
        DbType.DbInstant to "INSTANT",
        DbType.DbDateTime to "DATETIME"
    )

    val langToDataTypes = mapOf(
        Types.JBoolClass to DbType.DbBool,
        Types.JStringClass to DbType.DbString,
        Types.JShortClass to DbType.DbShort,
        Types.JIntClass to DbType.DbNumber,
        Types.JLongClass to DbType.DbLong,
        Types.JFloatClass to DbType.DbFloat,
        Types.JDoubleClass to DbType.DbDouble,
        // Types.JDecimalClass to  DbType.DbDecimal,
        Types.JLocalDateClass to DbType.DbLocalDate,
        Types.JLocalTimeClass to DbType.DbLocalTime,
        Types.JLocalDateTimeClass to DbType.DbLocalDateTime,
        Types.JZonedDateTimeClass to DbType.DbZonedDateTime,
        Types.JInstantClass to DbType.DbInstant,
        Types.JDateTimeClass to DbType.DbDateTime
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
    override fun addCol(name: String, dataType: DbType, required: Boolean, maxLen: Int): String {
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
    override fun colType(colType: DbType, maxLen: Int): String {
        return if (colType == DbType.DbString && maxLen == -1)
            "longtext"
        else if (colType == DbType.DbString)
            "VARCHAR($maxLen)"
        else
            getColTypeName(colType)
    }

    private fun build(name: String, prefix: String): String {
        val tableName = ensureField(name)
        val sql = "$prefix `$tableName`;"
        return sql
    }

    private fun getColTypeName(sqlType: DbType): String {
        return if (dataToColumnTypes.containsKey(sqlType))
            dataToColumnTypes[sqlType] ?: ""
        else
            throw UnexpectedException("Unexpected db type : $sqlType")
    }
}

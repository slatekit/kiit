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
import slatekit.common.db.DbTypeMap
import slatekit.common.newline
import slatekit.db.DbUtils.ensureField

/**
 * Builds up database tables, indexes and other database components
 */
open class PostGresBuilder : DbBuilder {

    val types = listOf(
        DbTypeMap(DbType.DbString, "NVARCHAR", Types.JStringClass),
        DbTypeMap(DbType.DbBool, "BIT", Types.JBoolClass),
        DbTypeMap(DbType.DbShort, "TINYINT", Types.JStringClass),
        DbTypeMap(DbType.DbNumber, "INTEGER", Types.JStringClass),
        DbTypeMap(DbType.DbLong, "BIGINT", Types.JStringClass),
        DbTypeMap(DbType.DbFloat, "FLOAT", Types.JStringClass),
        DbTypeMap(DbType.DbDouble, "DOUBLE", Types.JStringClass),
        DbTypeMap(DbType.DbDecimal, "DECIMAL", Types.JStringClass),
        DbTypeMap(DbType.DbLocalDate, "DATE", Types.JStringClass),
        DbTypeMap(DbType.DbLocalTime, "TIME", Types.JStringClass),
        DbTypeMap(DbType.DbLocalDateTime, "DATETIME", Types.JStringClass),
        DbTypeMap(DbType.DbZonedDateTime, "DATETIME", Types.JStringClass),
        DbTypeMap(DbType.DbInstant, "INSTANT", Types.JStringClass),
        DbTypeMap(DbType.DbDateTime, "DATETIME", Types.JStringClass)
    )

    /**
     * Mapping of normalized types ot postgres type names
     */
    val dataToColumnTypes = types.map { Pair(it.metaType, it.dbType) }.toMap()
    val langToDataTypes = types.map { Pair(it.langType, it.metaType) }.toMap()

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

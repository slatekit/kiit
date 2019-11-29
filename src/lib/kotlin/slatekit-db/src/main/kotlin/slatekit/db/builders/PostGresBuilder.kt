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
import slatekit.common.db.DbTypeInfo
import slatekit.common.newline
import slatekit.db.DbUtils.ensureField

/**
 * Builds up database tables, indexes and other database components
 */
open class PostGresBuilder : DbBuilder {

    val types = listOf(
        DbTypeInfo(DbFieldType.DbString, "NVARCHAR", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbBool, "BIT", Types.JBoolClass),
        DbTypeInfo(DbFieldType.DbShort, "TINYINT", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbNumber, "INTEGER", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbLong, "BIGINT", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbFloat, "FLOAT", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbDouble, "DOUBLE", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbDecimal, "DECIMAL", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbLocalDate, "DATE", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbLocalTime, "TIME", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbLocalDateTime, "DATETIME", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbZonedDateTime, "DATETIME", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbInstant, "INSTANT", Types.JStringClass),
        DbTypeInfo(DbFieldType.DbDateTime, "DATETIME", Types.JStringClass)
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

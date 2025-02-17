/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.db.builders

import java.rmi.UnexpectedException
import kiit.common.Types
import kiit.common.data.DataType
import kiit.common.data.DataTypeMap
import kiit.common.data.Encoding
import kiit.common.data.Vendor
import kiit.common.newline


/**
 * Builds up database tables, indexes and other database components
 */
open class PostGresBuilder : DbBuilder {

    val types = listOf(
        DataTypeMap(DataType.DTString, "NVARCHAR", Types.JStringClass),
        DataTypeMap(DataType.DTBool, "BIT", Types.JBoolClass),
        DataTypeMap(DataType.DTShort, "TINYINT", Types.JStringClass),
        DataTypeMap(DataType.DTInt, "INTEGER", Types.JStringClass),
        DataTypeMap(DataType.DTLong, "BIGINT", Types.JStringClass),
        DataTypeMap(DataType.DTFloat, "FLOAT", Types.JStringClass),
        DataTypeMap(DataType.DTDouble, "DOUBLE", Types.JStringClass),
        DataTypeMap(DataType.DTDecimal, "DECIMAL", Types.JStringClass),
        DataTypeMap(DataType.DTLocalDate, "DATE", Types.JStringClass),
        DataTypeMap(DataType.DTLocalTime, "TIME", Types.JStringClass),
        DataTypeMap(DataType.DTLocalDateTime, "DATETIME", Types.JStringClass),
        DataTypeMap(DataType.DTZonedDateTime, "DATETIME", Types.JStringClass),
        DataTypeMap(DataType.DTInstant, "INSTANT", Types.JStringClass),
        DataTypeMap(DataType.DTDateTime, "DATETIME", Types.JStringClass)
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

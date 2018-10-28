/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package slatekit.common.db.types

import slatekit.common.Types
import slatekit.common.db.*
import slatekit.common.db.DbUtils.ensureField
import slatekit.common.newline
import java.rmi.UnexpectedException

/**
 * Builds up database tables, indexes and other database components
 */
open class DbSourceMySql : DbSource {

    /**
     * Mapping of normalized types ot postgres type names
     */
    val dataToColumnTypes = mapOf(
        DbFieldTypeString to  "NVARCHAR",
        DbFieldTypeBool to  "BIT",
        DbFieldTypeShort to  "TINYINT",
        DbFieldTypeNumber to  "INTEGER",
        DbFieldTypeLong to  "BIGINT",
        DbFieldTypeFloat to  "FLOAT",
        DbFieldTypeDouble to  "DOUBLE",
        DbFieldTypeReal to  "DECIMAL",
        DbFieldTypeLocalDate to  "DATE",
        DbFieldTypeLocalTime to  "TIME",
        DbFieldTypeLocalDateTime to  "DATETIME",
        DbFieldTypeZonedDateTime to  "DATETIME",
        DbFieldTypeInstant to  "INSTANT",
        DbFieldTypeDateTime to  "DATETIME"
    )


    val langToDataTypes = mapOf(
        Types.JBoolClass to  DbFieldTypeBool,
        Types.JStringClass to  DbFieldTypeString,
        Types.JShortClass to  DbFieldTypeShort,
        Types.JIntClass to  DbFieldTypeNumber,
        Types.JLongClass to  DbFieldTypeLong,
        Types.JFloatClass to  DbFieldTypeFloat,
        Types.JDoubleClass to  DbFieldTypeDouble,
        Types.JDecimalClass to  DbFieldTypeReal,
        Types.JLocalDateClass to  DbFieldTypeLocalDate,
        Types.JLocalTimeClass to  DbFieldTypeLocalTime,
        Types.JLocalDateTimeClass to  DbFieldTypeLocalDateTime,
        Types.JZonedDateTimeClass to  DbFieldTypeZonedDateTime,
        Types.JInstantClass to  DbFieldTypeInstant,
        Types.JDateTimeClass to  DbFieldTypeDateTime
    )

    /**
     * Builds the drop table DDL for the name supplied.
     */
    override fun buildDropTable(name: String): String = build(name,"DROP TABLE IF EXISTS")

    /**
     * Builds a delete statement to delete all rows
     */
    override fun buildDeleteAll(name: String): String = build(name,"DELETE FROM")

    /**
     * Builds an add column DDL sql statement
     */
    override fun buildAddCol(name: String, dataType: DbFieldType, required: Boolean, maxLen: Int): String {
        val nullText = if (required) "NOT NULL" else ""
        val colType = buildColType(dataType, maxLen)
        val colName = buildColName(name)

        val sql = " $newline$colName $colType $nullText"
        return sql
    }

    /**
     * Builds a valid column name
     */
    override fun buildColName(name: String): String = "`" + ensureField(name) + "`"

    /**
     * Builds a valid column type
     */
    override fun buildColType(colType: DbFieldType, maxLen: Int): String {
        return if (colType == DbFieldTypeString && maxLen == -1)
            "longtext"
        else if (colType == DbFieldTypeString)
            "VARCHAR($maxLen)"
        else
            getColTypeName(colType)
    }


    private fun build(name:String, prefix:String): String {
        val tableName = ensureField(name)
        val sql = "$prefix `$tableName`;"
        return sql
    }

    private fun getColTypeName(sqlType: DbFieldType): String {
        return if(dataToColumnTypes.containsKey(sqlType))
            dataToColumnTypes[sqlType] ?: ""
        else
            throw UnexpectedException("Unexpected db type : $sqlType")
    }
}

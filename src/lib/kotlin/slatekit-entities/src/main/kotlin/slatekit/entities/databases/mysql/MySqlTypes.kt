package slatekit.entities.databases.mysql

import slatekit.common.Types
import slatekit.common.db.DbFieldType
import slatekit.common.db.types.DbTypeInfo

/**
 * MySql to Java types
 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
 */
object MySqlTypes {


    /**
     * BOOL
     */
    val boolType = DbTypeInfo(DbFieldType.DbBool, "BIT", Types.JBoolClass)

    /**
     * STRINGS
     */
    val charType = DbTypeInfo(DbFieldType.DbChar, "CHAR", Types.JCharClass)
    val stringType = DbTypeInfo(DbFieldType.DbString, "NVARCHAR", Types.JStringClass)
    val textType = DbTypeInfo(DbFieldType.DbText, "TEXT", Types.JStringClass)

    /**
     * UUID
     */
    val uuidType = DbTypeInfo(DbFieldType.DbString, "NVARCHAR", Types.JStringClass)

    /**
     * NUMBERS
     * https://dev.mysql.com/doc/refman/8.0/en/integer-types.html
     * Type	      Storage (Bytes)	Minimum Value Signed	Minimum Value Unsigned	Maximum Value Signed	Maximum Value Unsigned
     * TINYINT	  1	                -128	                0	                    127	                    255
     * SMALLINT	  2	                -32768	                0	                    32767	                65535
     * MEDIUMINT  3	                -8388608	            0	                    8388607	                16777215
     * INT	      4	                -2147483648	            0	                    2147483647	            4294967295
     * BIGINT	  8	                -263	                0	                    263-1	                264-1
     */
    val shortType = DbTypeInfo(DbFieldType.DbShort, "SMALLINT", Types.JShortClass)
    val intType = DbTypeInfo(DbFieldType.DbNumber, "INTEGER", Types.JIntClass)
    val longType = DbTypeInfo(DbFieldType.DbLong, "BIGINT", Types.JLongClass)
    val floatType = DbTypeInfo(DbFieldType.DbFloat, "FLOAT", Types.JFloatClass)
    val doubleType = DbTypeInfo(DbFieldType.DbDouble, "DOUBLE", Types.JDoubleClass)
    val decimalType = DbTypeInfo(DbFieldType.DbDecimal, "DECIMAL", Types.JDecimalClass)

    /**
     * DATES / TIMES
     */
    val localdateType = DbTypeInfo(DbFieldType.DbLocalDate, "DATE", Types.JLocalDateClass)
    val localtimeType = DbTypeInfo(DbFieldType.DbLocalTime, "TIME", Types.JLocalTimeClass)
    val localDateTimeType = DbTypeInfo(DbFieldType.DbLocalDateTime, "DATETIME", Types.JLocalDateTimeClass)
    val zonedDateTimeType = DbTypeInfo(DbFieldType.DbZonedDateTime, "DATETIME", Types.JZonedDateTimeClass)
    val dateTimeType = DbTypeInfo(DbFieldType.DbDateTime, "DATETIME", Types.JDateTimeClass)
    val instantType = DbTypeInfo(DbFieldType.DbInstant, "INSTANT", Types.JInstantClass)

    val lookup = mapOf(
            boolType.metaType to boolType,
            charType.metaType to charType,
            stringType.metaType to stringType,
            textType.metaType to textType,
            uuidType.metaType to uuidType,
            shortType.metaType to shortType,
            intType.metaType to intType,
            longType.metaType to longType,
            floatType.metaType to floatType,
            doubleType.metaType to doubleType,
            decimalType.metaType to decimalType,
            localdateType.metaType to localdateType,
            localtimeType.metaType to localtimeType,
            localDateTimeType.metaType to localDateTimeType,
            zonedDateTimeType.metaType to zonedDateTimeType,
            dateTimeType.metaType to dateTimeType,
            instantType.metaType to instantType
    )
}
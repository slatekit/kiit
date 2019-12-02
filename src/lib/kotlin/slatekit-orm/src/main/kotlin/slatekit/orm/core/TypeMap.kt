package slatekit.orm.core

import slatekit.common.Types
import slatekit.common.data.DbType
import slatekit.common.data.DbTypeMap

/**
 * MySql to Java types
 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
 */
open class TypeMap {

    /**
     * BOOL
     */
    open val boolType = DbTypeMap(DbType.DbBool, "BIT", Types.JBoolClass)

    /**
     * STRINGS
     */
    open val charType = DbTypeMap(DbType.DbChar, "CHAR", Types.JCharClass)
    open val stringType = DbTypeMap(DbType.DbString, "NVARCHAR", Types.JStringClass)
    open val textType = DbTypeMap(DbType.DbText, "TEXT", Types.JStringClass)

    /**
     * UUID
     */
    open val uuidType = DbTypeMap(DbType.DbString, "NVARCHAR", Types.JStringClass)

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
    open val shortType = DbTypeMap(DbType.DbShort, "SMALLINT", Types.JShortClass)
    open val intType = DbTypeMap(DbType.DbNumber, "INTEGER", Types.JIntClass)
    open val longType = DbTypeMap(DbType.DbLong, "BIGINT", Types.JLongClass)
    open val floatType = DbTypeMap(DbType.DbFloat, "FLOAT", Types.JFloatClass)
    open val doubleType = DbTypeMap(DbType.DbDouble, "DOUBLE", Types.JDoubleClass)
    //open val decimalType = DbTypeMap(DbType.DbDecimal, "DECIMAL", Types.JDecimalClass)

    /**
     * DATES / TIMES
     */
    open val localdateType = DbTypeMap(DbType.DbLocalDate, "DATE", Types.JLocalDateClass)
    open val localtimeType = DbTypeMap(DbType.DbLocalTime, "TIME", Types.JLocalTimeClass)
    open val localDateTimeType = DbTypeMap(DbType.DbLocalDateTime, "DATETIME", Types.JLocalDateTimeClass)
    open val zonedDateTimeType = DbTypeMap(DbType.DbZonedDateTime, "DATETIME", Types.JZonedDateTimeClass)
    open val dateTimeType = DbTypeMap(DbType.DbDateTime, "DATETIME", Types.JDateTimeClass)
    open val instantType = DbTypeMap(DbType.DbInstant, "INSTANT", Types.JInstantClass)

    open val lookup = mapOf(
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
            //decimalType.metaType to decimalType,
            localdateType.metaType to localdateType,
            localtimeType.metaType to localtimeType,
            localDateTimeType.metaType to localDateTimeType,
            zonedDateTimeType.metaType to zonedDateTimeType,
            dateTimeType.metaType to dateTimeType,
            instantType.metaType to instantType
    )
}
package slatekit.orm.databases

import slatekit.common.Types
import slatekit.common.db.DbFieldType
import slatekit.db.types.DbTypeInfo

/**
 * MySql to Java types
 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
 */
open class TypeMap {

    /**
     * BOOL
     */
    open val boolType = DbTypeInfo(DbFieldType.DbBool, "BIT", Types.JBoolClass)

    /**
     * STRINGS
     */
    open val charType = DbTypeInfo(DbFieldType.DbChar, "CHAR", Types.JCharClass)
    open val stringType = DbTypeInfo(DbFieldType.DbString, "NVARCHAR", Types.JStringClass)
    open val textType = DbTypeInfo(DbFieldType.DbText, "TEXT", Types.JStringClass)

    /**
     * UUID
     */
    open val uuidType = DbTypeInfo(DbFieldType.DbString, "NVARCHAR", Types.JStringClass)

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
    open val shortType = DbTypeInfo(DbFieldType.DbShort, "SMALLINT", Types.JShortClass)
    open val intType = DbTypeInfo(DbFieldType.DbNumber, "INTEGER", Types.JIntClass)
    open val longType = DbTypeInfo(DbFieldType.DbLong, "BIGINT", Types.JLongClass)
    open val floatType = DbTypeInfo(DbFieldType.DbFloat, "FLOAT", Types.JFloatClass)
    open val doubleType = DbTypeInfo(DbFieldType.DbDouble, "DOUBLE", Types.JDoubleClass)
    //open val decimalType = DbTypeInfo(DbFieldType.DbDecimal, "DECIMAL", Types.JDecimalClass)

    /**
     * DATES / TIMES
     */
    open val localdateType = DbTypeInfo(DbFieldType.DbLocalDate, "DATE", Types.JLocalDateClass)
    open val localtimeType = DbTypeInfo(DbFieldType.DbLocalTime, "TIME", Types.JLocalTimeClass)
    open val localDateTimeType = DbTypeInfo(DbFieldType.DbLocalDateTime, "DATETIME", Types.JLocalDateTimeClass)
    open val zonedDateTimeType = DbTypeInfo(DbFieldType.DbZonedDateTime, "DATETIME", Types.JZonedDateTimeClass)
    open val dateTimeType = DbTypeInfo(DbFieldType.DbDateTime, "DATETIME", Types.JDateTimeClass)
    open val instantType = DbTypeInfo(DbFieldType.DbInstant, "INSTANT", Types.JInstantClass)

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
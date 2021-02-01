package slatekit.data.core

import slatekit.common.Types
import slatekit.common.data.DataType
import slatekit.common.data.DataTypeMap

/**
 * Java types to MySql
 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
 */
open class Types {

    /**
     * BOOL
     */
    open val boolType = DataTypeMap(DataType.DTBool, "BIT", Types.JBoolClass)

    /**
     * STRINGS
     */
    open val charType = DataTypeMap(DataType.DTChar, "CHAR", Types.JCharClass)
    open val stringType = DataTypeMap(DataType.DTString, "NVARCHAR", Types.JStringClass)
    open val textType = DataTypeMap(DataType.DTText, "TEXT", Types.JStringClass)

    /**
     * UUID
     */
    open val uuidType = DataTypeMap(DataType.DTUUID, "NVARCHAR", Types.JStringClass)
    open val ulidType = DataTypeMap(DataType.DTULID, "NVARCHAR", Types.JStringClass)
    open val upidType = DataTypeMap(DataType.DTUPID, "NVARCHAR", Types.JStringClass)


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
    open val shortType = DataTypeMap(DataType.DTShort, "SMALLINT", Types.JShortClass)
    open val intType = DataTypeMap(DataType.DTInt, "INTEGER", Types.JIntClass)
    open val longType = DataTypeMap(DataType.DTLong, "BIGINT", Types.JLongClass)
    open val floatType = DataTypeMap(DataType.DTFloat, "FLOAT", Types.JFloatClass)
    open val doubleType = DataTypeMap(DataType.DTDouble, "DOUBLE", Types.JDoubleClass)
    //open val decimalType = DataTypeMap(DataType.DbDecimal, "DECIMAL", Types.JDecimalClass)

    /**
     * DATES / TIMES
     */
    open val localdateType = DataTypeMap(DataType.DTLocalDate, "DATE", Types.JLocalDateClass)
    open val localtimeType = DataTypeMap(DataType.DTLocalTime, "TIME", Types.JLocalTimeClass)
    open val localDateTimeType = DataTypeMap(DataType.DTLocalDateTime, "DATETIME", Types.JLocalDateTimeClass)
    open val zonedDateTimeType = DataTypeMap(DataType.DTZonedDateTime, "DATETIME", Types.JZonedDateTimeClass)
    open val dateTimeType = DataTypeMap(DataType.DTDateTime, "DATETIME", Types.JDateTimeClass)
    open val instantType = DataTypeMap(DataType.DTInstant, "INSTANT", Types.JInstantClass)


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
            instantType.metaType to instantType,
            uuidType.metaType to uuidType,
            ulidType.metaType to ulidType,
            upidType.metaType to upidType
    )
}

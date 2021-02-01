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
package slatekit.common.data

/**
 * Non-Reflection/Class based representation of data types.
 */
sealed class DataType {

    /* ktlint-disable */
    object DTBool          : DataType()

    // Chars/Strings
    object DTChar          : DataType()
    object DTString        : DataType()
    object DTText          : DataType()

    // Numbers
    object DTShort         : DataType()
    object DTInt           : DataType()
    object DTLong          : DataType()
    object DTFloat         : DataType()
    object DTDouble        : DataType()
    object DTDecimal       : DataType()

    // Dates
    object DTLocalDate     : DataType()
    object DTLocalTime     : DataType()
    object DTLocalDateTime : DataType()
    object DTZonedDateTime : DataType()
    object DTInstant       : DataType()
    object DTDateTime      : DataType()

    // Misc
    object DTEnum          : DataType()
    object DTUUID          : DataType()
    object DTULID          : DataType()
    object DTUPID          : DataType()

    // Object
    object DTObject        : DataType()

    companion object {
        fun fromJava(dataType: Class<*>): DataType =
                if (dataType == slatekit.common.Types.JBoolClass) DataType.DTBool
                else if (dataType == slatekit.common.Types.JStringClass) DataType.DTString
                else if (dataType == slatekit.common.Types.JShortClass) DataType.DTShort
                else if (dataType == slatekit.common.Types.JIntClass) DataType.DTInt
                else if (dataType == slatekit.common.Types.JLongClass) DataType.DTLong
                else if (dataType == slatekit.common.Types.JFloatClass) DataType.DTFloat
                else if (dataType == slatekit.common.Types.JDoubleClass) DataType.DTDouble
                // else if (dataType == slatekit.common.Types.JDecimalClass) DataType.DbDecimal
                else if (dataType == slatekit.common.Types.JLocalDateClass) DataType.DTLocalDate
                else if (dataType == slatekit.common.Types.JLocalTimeClass) DataType.DTLocalTime
                else if (dataType == slatekit.common.Types.JLocalDateTimeClass) DataType.DTLocalDateTime
                else if (dataType == slatekit.common.Types.JZonedDateTimeClass) DataType.DTZonedDateTime
                else if (dataType == slatekit.common.Types.JInstantClass) DataType.DTInstant
                else if (dataType == slatekit.common.Types.JDateTimeClass) DataType.DTDateTime
                else if (dataType == slatekit.common.Types.JUUIDClass) DataType.DTUUID
                else if (dataType == slatekit.common.Types.JULIDClass) DataType.DTULID
                else if (dataType == slatekit.common.Types.JUPIDClass) DataType.DTUPID
                else DataType.DTObject
    }
    /* ktlint-enable */
}


data class DataTypeMap(val metaType: DataType, val dbType: String, val langType: Class<*>)





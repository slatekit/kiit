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
package kiit.common.data

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
                if (dataType == kiit.common.Types.JBoolClass) DataType.DTBool
                else if (dataType == kiit.common.Types.JStringClass) DataType.DTString
                else if (dataType == kiit.common.Types.JShortClass) DataType.DTShort
                else if (dataType == kiit.common.Types.JIntClass) DataType.DTInt
                else if (dataType == kiit.common.Types.JLongClass) DataType.DTLong
                else if (dataType == kiit.common.Types.JFloatClass) DataType.DTFloat
                else if (dataType == kiit.common.Types.JDoubleClass) DataType.DTDouble
                // else if (dataType == kiit.common.Types.JDecimalClass) DataType.DbDecimal
                else if (dataType == kiit.common.Types.JLocalDateClass) DataType.DTLocalDate
                else if (dataType == kiit.common.Types.JLocalTimeClass) DataType.DTLocalTime
                else if (dataType == kiit.common.Types.JLocalDateTimeClass) DataType.DTLocalDateTime
                else if (dataType == kiit.common.Types.JZonedDateTimeClass) DataType.DTZonedDateTime
                else if (dataType == kiit.common.Types.JInstantClass) DataType.DTInstant
                else if (dataType == kiit.common.Types.JDateTimeClass) DataType.DTDateTime
                else if (dataType == kiit.common.Types.JUUIDClass) DataType.DTUUID
                else if (dataType == kiit.common.Types.JULIDClass) DataType.DTULID
                else if (dataType == kiit.common.Types.JUPIDClass) DataType.DTUPID
                else DataType.DTObject
    }
    /* ktlint-enable */
}


data class DbTypeMap(val vendor: Vendor, val types:List<DataTypeMap>)
data class DataTypeMap(val metaType: DataType, val dbType: String, val langType: Class<*>)





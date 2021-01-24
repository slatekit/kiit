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

sealed class DataType {

    /* ktlint-disable */
    object DbBool          : DataType()
    object DbChar          : DataType()
    object DbString        : DataType()
    object DbText          : DataType()
    object DbShort         : DataType()
    object DbNumber        : DataType()
    object DbLong          : DataType()
    object DbFloat         : DataType()
    object DbDouble        : DataType()
    object DbDecimal       : DataType()
    object DbLocalDate     : DataType()
    object DbLocalTime     : DataType()
    object DbLocalDateTime : DataType()
    object DbZonedDateTime : DataType()
    object DbInstant       : DataType()
    object DbDateTime      : DataType()
    object DbEnum          : DataType()

    companion object {
        fun getTypeFromLang(dataType: Class<*>): DataType =
                if (dataType == slatekit.common.Types.JBoolClass) DataType.DbBool
                else if (dataType == slatekit.common.Types.JStringClass) DataType.DbString
                else if (dataType == slatekit.common.Types.JShortClass) DataType.DbShort
                else if (dataType == slatekit.common.Types.JIntClass) DataType.DbNumber
                else if (dataType == slatekit.common.Types.JLongClass) DataType.DbLong
                else if (dataType == slatekit.common.Types.JFloatClass) DataType.DbFloat
                else if (dataType == slatekit.common.Types.JDoubleClass) DataType.DbDouble
                // else if (dataType == Types.JDecimalClass) DataType.DbDecimal
                else if (dataType == slatekit.common.Types.JLocalDateClass) DataType.DbLocalDate
                else if (dataType == slatekit.common.Types.JLocalTimeClass) DataType.DbLocalTime
                else if (dataType == slatekit.common.Types.JLocalDateTimeClass) DataType.DbLocalDateTime
                else if (dataType == slatekit.common.Types.JZonedDateTimeClass) DataType.DbZonedDateTime
                else if (dataType == slatekit.common.Types.JInstantClass) DataType.DbInstant
                else if (dataType == slatekit.common.Types.JDateTimeClass) DataType.DbDateTime
                else DataType.DbString
    }
    /* ktlint-enable */
}




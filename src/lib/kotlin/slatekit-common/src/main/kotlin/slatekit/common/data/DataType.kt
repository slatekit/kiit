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
    /* ktlint-enable */
}




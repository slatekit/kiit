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

sealed class DbType {

    /* ktlint-disable */
    object DbBool          : DbType()
    object DbChar          : DbType()
    object DbString        : DbType()
    object DbText          : DbType()
    object DbShort         : DbType()
    object DbNumber        : DbType()
    object DbLong          : DbType()
    object DbFloat         : DbType()
    object DbDouble        : DbType()
    object DbDecimal       : DbType()
    object DbLocalDate     : DbType()
    object DbLocalTime     : DbType()
    object DbLocalDateTime : DbType()
    object DbZonedDateTime : DbType()
    object DbInstant       : DbType()
    object DbDateTime      : DbType()
    object DbEnum          : DbType()
    /* ktlint-enable */
}




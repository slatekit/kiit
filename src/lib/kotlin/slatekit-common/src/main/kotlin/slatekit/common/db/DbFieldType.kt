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
package slatekit.common.db

sealed class DbFieldType {

    object DbBool : DbFieldType()

    object DbChar : DbFieldType()
    object DbString : DbFieldType()
    object DbText : DbFieldType()

    object DbShort : DbFieldType()
    object DbNumber : DbFieldType()
    object DbLong : DbFieldType()
    object DbFloat : DbFieldType()
    object DbDouble : DbFieldType()
    object DbDecimal : DbFieldType()

    object DbLocalDate : DbFieldType()
    object DbLocalTime : DbFieldType()
    object DbLocalDateTime : DbFieldType()
    object DbZonedDateTime : DbFieldType()
    object DbInstant : DbFieldType()
    object DbDateTime : DbFieldType()

    object DbEnum : DbFieldType()
}

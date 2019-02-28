/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slatekit.common.db

sealed class DbFieldType {
    object DbNumber : DbFieldType()
    object DbShort : DbFieldType()
    object DbLong : DbFieldType()
    object DbChar : DbFieldType()
    object DbString : DbFieldType()
    object DbText : DbFieldType()
    object DbBool : DbFieldType()
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

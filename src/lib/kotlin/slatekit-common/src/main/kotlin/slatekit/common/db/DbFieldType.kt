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

/*
* val TypeNumber      = 0
  val TypeNumberShort = 1
  val TypeNumberLong  = 2
  val TypeString      = 10
  val TypeBool        = 11
  val TypeReal        = 12
  val TypeDate        = 20
  val TypeTime        = 21
  val TypeEnum        = 22
* */
interface DbFieldType

object DbFieldTypeNumber        : DbFieldType
object DbFieldTypeShort         : DbFieldType
object DbFieldTypeLong          : DbFieldType
object DbFieldTypeString        : DbFieldType
object DbFieldTypeBool          : DbFieldType
object DbFieldTypeReal          : DbFieldType
object DbFieldTypeLocalDate     : DbFieldType
object DbFieldTypeLocalTime     : DbFieldType
object DbFieldTypeLocalDateTime : DbFieldType
object DbFieldTypeZonedDateTime : DbFieldType
object DbFieldTypeInstant       : DbFieldType
object DbFieldTypeDateTime      : DbFieldType
object DbFieldTypeEnum          : DbFieldType
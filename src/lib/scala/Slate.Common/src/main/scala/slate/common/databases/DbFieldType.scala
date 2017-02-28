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
package slate.common.databases

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
trait DbFieldType
case object DbFieldTypeNumber      extends DbFieldType
case object DbFieldTypeShort       extends DbFieldType
case object DbFieldTypeLong        extends DbFieldType
case object DbFieldTypeString      extends DbFieldType
case object DbFieldTypeBool        extends DbFieldType
case object DbFieldTypeReal        extends DbFieldType
case object DbFieldTypeDate        extends DbFieldType
case object DbFieldTypeTime        extends DbFieldType
case object DbFieldTypeEnum        extends DbFieldType
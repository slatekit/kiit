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

import slatekit.common.DateTime
import slatekit.common.TimeSpan
import slatekit.common.kClass
import kotlin.reflect.KClass


object DbFuncs {


  fun getTypeFromLang(dataType: KClass<*>):DbFieldType =
    if      (dataType == Boolean::class ) DbFieldTypeBool
    else if (dataType == String::class  ) DbFieldTypeString
    else if (dataType == Short::class   ) DbFieldTypeShort
    else if (dataType == Int::class     ) DbFieldTypeNumber
    else if (dataType == Long::class    ) DbFieldTypeLong
    else if (dataType == Double::class  ) DbFieldTypeReal
    else if (dataType == DateTime::class) DbFieldTypeDate
    else if (dataType == TimeSpan::class) DbFieldTypeTime
    else DbFieldTypeString


  fun ensureField(text:String): String =
    if(text.isNullOrEmpty())
      ""
    else {
      text.toLowerCase().trim().filter{ c -> c.isDigit() || c.isLetter() || c == '_'}
    }

}

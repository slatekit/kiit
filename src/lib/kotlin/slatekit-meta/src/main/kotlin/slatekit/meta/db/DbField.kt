/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.meta.db

import slatekit.common.Types
import slatekit.meta.KTypes
import kotlin.reflect.KClass

/**
  * Internal USE ONLY
  */
data class DbField(
                    val name       :String = "",
                    val dataType   :String = "",
                    val nullable   :String = "",
                    val key        :String = "",
                    val defaultVal :String = "",
                    val extra      :String = ""
                  )
{
  val isNull :Boolean get() = "NO".compareTo(nullable, true) == 0


  val isKey  :Boolean get() = "PRI".compareTo(key, true) == 0


  fun getFieldType(): KClass<*> =

    when (dataType) {
      "int(11)"      -> KTypes.KIntClass
      "int(15)"      -> KTypes.KLongClass
      "int(6)"       -> KTypes.KIntClass
      "tinyint(1)"   -> KTypes.KShortClass
      "bit(1)"       -> KTypes.KBoolClass
      "date"         -> KTypes.KLocalDateClass
      "time"         -> KTypes.KLocalTimeClass
      "datetime"     -> KTypes.KLocalDateTimeClass
      "instant"      -> KTypes.KInstantClass
      "longtext"     -> KTypes.KStringClass
      else           -> KTypes.KStringClass
    }



  fun maxLength():Int =
    when ( dataType ) {
      "longtext"     -> -1
      else           -> if(isVar(dataType)) lengthFromVar(dataType) else -1
    }


  fun isVar(s:String): Boolean = s.startsWith("varchar")


  fun lengthFromVar(s:String):Int =
      s.replace(")", "").replace("varchar(", "").toInt()


  override fun toString(): String =
    name +
    ", " + dataType   +
    ", " + nullable   +
    ", " + key        +
    ", " + defaultVal +
    ", " + extra

}

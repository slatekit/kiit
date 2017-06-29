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

package slatekit.common.db

import slatekit.common.DateTime
import slatekit.common.kClass
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


  fun getFieldType(): KClass<Any> =

    when (dataType) {
      "int(11)"      -> Int::class.kClass
      "int(15)"      -> Long::class.kClass
      "int(6)"       -> Int::class.kClass
      "tinyint(1)"   -> Short::class.kClass
      "bit(1)"       -> Boolean::class.kClass
      "datetime"     -> DateTime::class.kClass
      "longtext"     -> String::class.kClass
      else           -> String::class.kClass
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

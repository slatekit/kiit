package kiit.query

import kiit.common.EnumLike
import kiit.common.EnumSupport


enum class Order(val text:String, override val value:Int) : EnumLike {
    Asc ("asc", 0),
    Dsc("desc", 1);

    companion object : EnumSupport()  {
        override fun all(): Array<EnumLike> {
            return arrayOf(Asc, Dsc)
        }
    }
}

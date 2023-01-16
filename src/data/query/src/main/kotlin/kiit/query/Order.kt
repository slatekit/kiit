package kiit.query

import slatekit.common.EnumLike
import slatekit.common.EnumSupport


enum class Order(val text:String, override val value:Int) : EnumLike {
    Asc ("asc", 0),
    Dsc("desc", 1);

    companion object : EnumSupport()  {
        override fun all(): Array<EnumLike> {
            return arrayOf(Asc, Dsc)
        }
    }
}

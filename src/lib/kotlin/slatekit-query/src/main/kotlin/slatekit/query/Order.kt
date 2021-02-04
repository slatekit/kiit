package slatekit.query

import slatekit.common.EnumLike
import slatekit.common.EnumSupport


enum class Order(override val value:Int) : EnumLike {
    Asc (0),
    Dsc(1);

    companion object : EnumSupport()  {
        override fun all(): Array<EnumLike> {
            return arrayOf(Asc, Dsc)
        }
    }
}

package slatekit.app

import slatekit.common.EnumLike
import slatekit.common.EnumSupport


enum class ErrorMode(override val value:Int) : EnumLike {
    Rethrow(0),
    Print (1),
    Store(2);


    companion object : EnumSupport()  {

        override fun all(): Array<EnumLike> {
            return arrayOf(Rethrow, Print, Store)
        }
    }
}
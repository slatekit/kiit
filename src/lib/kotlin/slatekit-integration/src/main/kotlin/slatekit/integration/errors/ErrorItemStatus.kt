package slatekit.integration.errors

import slatekit.common.EnumLike
import slatekit.common.EnumSupport


enum class ErrorItemStatus(override val value:Int) : EnumLike {
    Active(0),
    Retrying (1),
    Succeeded(2);


    companion object : EnumSupport()  {

        override fun all(): Array<EnumLike> {
            return arrayOf(Active, Retrying, Succeeded)
        }
    }
}

package slatekit.samples.common.models

import slatekit.common.EnumLike
import slatekit.common.EnumSupport

enum class Delivery(override val value:Int) : EnumLike {
        Theater (0),
        Streamed(1),
        Direct  (2);

        companion object : EnumSupport()  {
                override fun all(): Array<EnumLike> {
                        return arrayOf(Theater, Streamed, Direct)
                }
        }
}
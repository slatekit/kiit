package kiit.common.auth

import kiit.common.EnumLike
import kiit.common.EnumSupport


enum class TokenType(override val value:Int) : EnumLike {
    Identity(0),
    Access(1),
    Refresh(2);


    companion object : EnumSupport()  {

        override fun all(): Array<EnumLike> {
            return arrayOf(Identity, Access, Refresh)
        }
    }
}
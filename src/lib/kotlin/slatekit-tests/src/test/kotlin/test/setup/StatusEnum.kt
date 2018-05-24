package test.setup

import slatekit.common.EnumLike
import slatekit.common.EnumSupport


enum class StatusEnum(override val value:Int) : EnumLike {
    Pending(0),
    Active (1),
    Blocked(2);


    companion object : EnumSupport()  {

        override fun all(): Array<EnumLike> {
            return arrayOf(Pending, Active, Blocked)
        }
    }
}



enum class RoleEnum(val value:Int) {
    Member(0),
    Admin(1);
}



data class MemberData(val name:String, val id:Int, val status:StatusEnum, val role:RoleEnum)


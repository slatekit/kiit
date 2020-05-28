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



data class StatusEnum2(
        override val name:String,
        override val value:Int
) : EnumLike {

    companion object : EnumSupport()  {

        val Pending     = StatusEnum2( "Pending", 0 )
        val Active      = StatusEnum2( "Active" , 1 )
        val Blocked     = StatusEnum2( "Blocked", 2 )


        override fun all(): Array<EnumLike> {
            return arrayOf(Pending, Active, Blocked)
        }


        override fun isUnknownSupported(): Boolean {
            return true
        }


        override fun unknown(name:String): EnumLike {
            return StatusEnum2(name, 7)
        }


        override fun unknown(value:Int): EnumLike {
            return StatusEnum2("unknown", 7)
        }
    }
}


sealed class StatusEnum3(override val name:String, override val value:Int) : EnumLike {

    object Pending    : StatusEnum3( "Pending", 0 )
    object Active     : StatusEnum3( "Active" , 1 )
    object Blocked    : StatusEnum3( "Blocked", 2 )


    companion object : EnumSupport()  {

        val allItems:Array<EnumLike> by lazy { arrayOf<EnumLike>(Pending, Active, Blocked) }

        override fun all(): Array<EnumLike> {
            return allItems
        }
    }
}



enum class RoleEnum(val value:Int) {
    Member(0),
    Admin(1);
}



data class MemberData(val name:String, val id:Int, val status:StatusEnum, val role:RoleEnum)


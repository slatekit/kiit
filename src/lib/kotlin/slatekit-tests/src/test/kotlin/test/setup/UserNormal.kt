/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.setup

import slatekit.common.Random
import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.entities.core.Entity
import slatekit.entities.core.EntityUpdatable
import slatekit.entities.core.EntityWithId

/**
 * Created by kishorereddy on 5/23/17.
 */

data class User(

        val id:Long,

        val email:String,

        val firstName:String,

        val lastName:String,

        val male:Boolean,

        val age:Int
) : Entity
{
    override fun identity():Long = id
}


data class Group(
        @property:Field(required = true)
        val id:Long,

        @property:Field(required = true, length = 30)
        val name:String
) : Entity
{
    override fun identity():Long = id
}


data class Member(
        @property:Field(required = true)
        val id:Long,

        @property:Field(required = true)
        val groupId:Long,

        @property:Field(required = true)
        val userId:Long
) : Entity
{
    override fun identity():Long = id
}


class UserNormal1 {


    var email:String = ""

    var id:Long = 0

    var active:Boolean = false

    var age:Int = 0

    var salary:Double = 0.0

    var starts: DateTime = DateTime.now()
}


class UserNormal2(var email:String, var name:String) {

}


data class User3(

                    @property:Field(name = "email", desc = "email address", required = true, eg = "clark@metro.com")
                    val email:String,

                    @property:Field(desc = "full api", required = false, eg = "clark kent")
                    val name:String
                )
{

}


data class User4(

        val email:String,

        val id:Long,

        val active:Boolean,

        val age:Int,

        val salary:Double,

        val starts: DateTime
)
{
    fun activate(ident:Long, actived:Boolean, started:DateTime):Long {
        return 0L
    }
}


data class User5(
        @property:Field()
        override val id: Long = 0,

        @property:Field(required = true)
        val email:String = "",

        @property:Field(required = true)
        val isActive:Boolean = false,

        @property:Field(required = true)
        val age:Int = 35,

        @property:Field(required = true)
        val salary:Double = 20.5,

        @property:Field(required = true)
        val createdAt: DateTime = DateTime.now(),

        @property:Field(required = true)
        val createdBy: Long = 0,

        @property:Field(required = true)
        val updatedAt: DateTime = DateTime.now(),

        @property:Field(required = true)
        val updatedBy: Long = 0,

        @property:Field(required = true)
        val uniqueId: String            = Random.stringGuid()
) : EntityWithId, EntityUpdatable<User5> {
    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    override fun withId(id:Long): User5 {
        return this.copy(id)
    }
}
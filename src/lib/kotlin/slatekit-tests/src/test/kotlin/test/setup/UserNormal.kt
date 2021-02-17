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

import slatekit.common.utils.Random
import slatekit.common.DateTime
import slatekit.entities.*

/**
 * Created by kishorereddy on 5/23/17.
 */

data class User(

        val id: Long,

        val email: String,

        val firstName: String,

        val lastName: String,

        val male: Boolean,

        val age: Int
) : Entity<Long> {
    override fun isPersisted(): Boolean = id > 0

    override fun identity(): Long = id
}


data class Group(
        @property:Id()
        val id: Long,

        @property:Column(required = true, length = 30)
        val name: String
) : Entity<Long>, EntityUpdatable<Long, Group> {

    override fun isPersisted(): Boolean = id > 0
    override fun identity(): Long = id
    override fun withId(id: Long): Group = this.copy(id = id)
}


data class Member(
        @property:Column(required = true)
        val id: Long,

        @property:Column(required = true)
        val groupId: Long,

        @property:Column(required = true)
        val userId: Long
) : Entity<Long>, EntityUpdatable<Long, Member> {

    override fun isPersisted(): Boolean = id > 0
    override fun identity(): Long = id
    override fun withId(id: Long): Member = this.copy(id = id)
}


class UserNormal1 {


    var email: String = ""

    var id: Long = 0

    var active: Boolean = false

    var age: Int = 0

    var salary: Double = 0.0

    var starts: DateTime = DateTime.now()
}


class UserNormal2(var email: String, var name: String) {

}


data class User3(

        @property:Column(name = "email", desc = "email address", required = true, example = "clark@metro.com")
        val email: String,

        @property:Column(desc = "full api", required = false, example = "clark kent")
        val name: String
) {

}


data class User4(

        val email: String,

        val id: Long,

        val active: Boolean,

        val age: Int,

        val salary: Double,

        val starts: DateTime
) {
    fun activate(ident: Long, actived: Boolean, started: DateTime): Long {
        return 0L
    }
}


data class User5(
        @property:Id()
        override val id: Long = 0,

        @property:Column(length = 100, required = true)
        val email: String = "",

        @property:Column(required = true)
        val isActive: Boolean = false,

        @property:Column(required = true)
        val level: Int = 35,

        @property:Column(required = true)
        val salary: Double = 20.5,

        @property:Column(required = true)
        val createdAt: DateTime = DateTime.now(),

        @property:Column(required = true)
        val createdBy: Long = 0,

        @property:Column(required = true)
        val updatedAt: DateTime = DateTime.now(),

        @property:Column(required = true)
        val updatedBy: Long = 0,

        @property:Column(length = 50, required = true)
        val uniqueId: String = Random.uuid()
) : EntityWithId<Long>, EntityUpdatable<Long, User5> {

    override fun isPersisted(): Boolean = id > 0

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    override fun withId(id: Long): User5 {
        return this.copy(id = id)
    }
}
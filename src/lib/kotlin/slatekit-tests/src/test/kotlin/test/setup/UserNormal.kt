/**
 <kiit_header>
url: www.kiit.dev
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package test.setup

import kiit.common.utils.Random
import kiit.common.DateTime
import kiit.entities.*

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
        @Id()
        val id: Long,

        @Column(required = true, length = 30)
        val name: String
) : Entity<Long>, EntityUpdatable<Long, Group> {

    override fun isPersisted(): Boolean = id > 0
    override fun identity(): Long = id
    override fun withId(id: Long): Group = this.copy(id = id)
}


data class Member(
        @Column(required = true)
        val id: Long,

        @Column(required = true)
        val groupId: Long,

        @Column(required = true)
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

        @Column(name = "email", desc = "email address", required = true, example = "clark@metro.com")
        val email: String,

        @Column(desc = "full api", required = false, example = "clark kent")
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


@Table("user", schema = "unit_tests")
data class User5(
        @Id()
        override val id: Long = 0,

        @Column(length = 50, required = true)
        val userId: String = Random.uuid(),

        @Column(length = 100, required = true)
        val email: String = "",

        @Column(required = true)
        val isActive: Boolean = false,

        @Column(required = true)
        val level: Int = 35,

        @Column(required = true)
        val salary: Double = 20.5,

        @Column(name = "createdat", required = true)
        val createdAt: DateTime = DateTime.now(),

        @Column(name = "createdby", required = true)
        val createdBy: Long = 0,

        @Column(name = "updatedat", required = true)
        val updatedAt: DateTime = DateTime.now(),

        @Column(name = "updatedby", required = true)
        val updatedBy: Long = 0
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



data class UserNullable(
    @Id()
    override val id: Long = 0,

    @Column(length = 50, required = true)
    val uuid: String = Random.uuid(),

    @Column(length = 100, required = false)
    val email: String? = null,

    @Column(required = false)
    val isActive: Boolean? = null,

    @Column(required = false)
    val level: Int? = null,

    @Column(required = false)
    val salary: Double? = null,

    @Column(required = false)
    val createdAt: DateTime? = null,

    @Column(required = false)
    val createdBy: Long? = null,

    @Column(required = false)
    val updatedAt: DateTime? = null,

    @Column(required = false)
    val updatedBy: Long? = null
) : EntityWithId<Long>, EntityUpdatable<Long, UserNullable> {

    override fun isPersisted(): Boolean = id > 0

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    override fun withId(id: Long): UserNullable {
        return this.copy(id = id)
    }
}
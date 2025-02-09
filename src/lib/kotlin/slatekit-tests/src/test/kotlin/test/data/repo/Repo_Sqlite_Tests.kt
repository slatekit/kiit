/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
 *  </kiit_header>
 */

package test.data.repo

import org.junit.Before
import kiit.common.DateTime
import kiit.common.data.*
import kiit.common.utils.Random
import kiit.entities.*
import kiit.entities.features.Counts
import kiit.entities.features.Ordered
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import test.entities.EntitySetup
import test.setup.User5


@Ignore
class Repo_Sqlite_Tests : Repo_Common_Tests() {

    /**
     * This provides the implementation for the repo ( using mysql as vendor )
     */
    override fun process(op: (EntityRepo<Long, User5>) -> Unit) {
        val con = DbConString(Vendor.SqLite, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
        val db = EntitySetup.db(Vendor.SqLite)
        val entities = Entities( {_ -> db } )
        val repo = entities.repo(EntityLongId(), Long::class, User5::class, "user", null, Vendor.SqLite)
        runBlocking {
            op(repo)
        }
    }

    @Before
    fun setup() {
//        val con = DbConString(Vendor.SqLite, "jdbc:sqlite::memory:?cache=shared", "", "")
//        val cons = Connections.of(con)
//        val db = Db.of(con)
//        db.execute(USERS_DDL.sqliteIfNotExists("User"))
//        db.execute(GROUP_DDL.sqliteIfNotExists("Group"))
//        db.execute(MEMBER_DDL.sqliteIfNotExists("Member"))
//
//        entities = Entities({ _ -> db }, cons, MyEncryptor)
//        entities.register<Int, User>(EntityIntId(), vendor = Vendor.SqLite) { repo -> UserServiceInt(repo) }
//        entities.register<Int, Member>(EntityIntId(), vendor = Vendor.SqLite) { repo -> EntityService(repo) }
//        entities.register<Int, Group>(EntityIntId(), vendor = Vendor.SqLite) { repo -> EntityService(repo) }
    }


    companion object {
        val USERS_DDL = """
            CREATE TABLE "User" (
              "id" integer PRIMARY KEY AUTOINCREMENT,
              "email" text NOT NULL,
              "isactive" integer NOT NULL,
              "level" integer DEFAULT NULL,
              "salary" real NOT NULL,
              "createdat" real NOT NULL,
              "createdby" integer NOT NULL,
              "updatedat" real NOT NULL,
              "updatedby" integer NOT NULL,
              "uniqueid" text NOT NULL
            );
        """.trimIndent()


        val GROUP_DDL = """
            CREATE TABLE "Group" (
              "id" integer PRIMARY KEY AUTOINCREMENT,
              "name" text NOT NULL
            );
        """.trimIndent()


        val MEMBER_DDL = """
            CREATE TABLE "Member" (
              "id" integer PRIMARY KEY  AUTOINCREMENT,
              "groupid" integer NOT NULL,
              "userid" integer NOT NULL
            );
        """.trimIndent()
    }



    data class User(
            @property:Id()
            override val id: Int = 0,

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
            val createdBy: Int = 0,

            @property:Column(required = true)
            val updatedAt: DateTime = DateTime.now(),

            @property:Column(required = true)
            val updatedBy: Int = 0,

            @property:Column(length = 50, required = true)
            val uniqueId: String = Random.uuid()
    ) : EntityWithId<Int>, EntityUpdatable<Int, User> {

        override fun isPersisted(): Boolean = id > 0
        override fun withId(id: Int): User = this.copy(id = id)
    }


    data class Group(
            @property:Column(required = true)
            val id: Int,

            @property:Column(required = true, length = 30)
            val name: String
    ) : Entity<Int>, EntityUpdatable<Int, Group> {

        override fun isPersisted(): Boolean = id > 0
        override fun identity(): Int = id
        override fun withId(id: Int): Group = this.copy(id = id)
    }


    data class Member(
            @property:Column(required = true)
            val id: Int,

            @property:Column(required = true)
            val groupId: Int,

            @property:Column(required = true)
            val userId: Int
    ) : Entity<Int>, EntityUpdatable<Int, Member> {

        override fun isPersisted(): Boolean = id > 0
        override fun identity(): Int = id
        override fun withId(id: Int): Member = this.copy(id = id)
    }


    class UserServiceInt(repo: EntityRepo<Int, User>) : EntityService<Int, User>(repo), Ordered<Int, User>, Counts<Int, User>

}

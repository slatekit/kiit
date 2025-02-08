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

package test.entities

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kiit.common.DateTime
import kiit.common.data.*
import kiit.common.utils.Random
import kiit.data.sql.vendors.sqliteIfNotExists
import kiit.db.Db
import kiit.entities.*
import kiit.entities.features.Counts
import kiit.entities.features.Ordered
import kiit.query.Op
import kiit.query.set
import kiit.query.where
import test.entities.mysql.Data_04_Entity_Service_MySql
import test.setup.MyEncryptor


class Data_04_Entity_Service_Sqlite : Data_04_Entity_Service_MySql() {


    @Before
    override fun setup() {
        val con = DbConString(Vendor.SqLite, "jdbc:sqlite::memory:?cache=shared", "", "")
        val cons = Connections.of(con)
        val db = Db.of(con)
        db.execute(USERS_DDL.sqliteIfNotExists("User"))
        db.execute(GROUP_DDL.sqliteIfNotExists("Group"))
        db.execute(MEMBER_DDL.sqliteIfNotExists("Member"))

        entities = Entities({ _ -> db }, cons, MyEncryptor)
        entities.register<Int, User>(EntityIntId(), vendor = Vendor.SqLite) { repo -> UserServiceInt(repo) }
        entities.register<Int, Member>(EntityIntId(), vendor = Vendor.SqLite) { repo -> EntityService(repo) }
        entities.register<Int, Group>(EntityIntId(), vendor = Vendor.SqLite) { repo -> EntityService(repo) }
    }


    @Test
    override fun can_create_an_item() {
        runBlocking {
            val userSvc = entities.getService<Int, User>() as UserServiceInt
            val memsSvc = entities.getService<Int, Member>()
            val grpSvc = entities.getService<Int, Group>()

            userSvc.deleteAll()
            memsSvc.deleteAll()
            grpSvc.deleteAll()
            val id = userSvc.create(User(0, "test_create@abc.com", true, 35, 12.34))
            val User = userSvc.getById(id)
            Assert.assertTrue(User != null)
            Assert.assertTrue(User?.email == "test_create@abc.com")
            Assert.assertTrue(User?.isActive == true)
            Assert.assertTrue(User?.level == 35)
            Assert.assertTrue(User?.salary == 12.34)
        }
    }


    @Test
    override fun can_update_an_item() {
        val lc = kiit.common.Types.JLongClass
        val kc = Long::class.java
        val isEqual = lc == kc
        println(isEqual)
        runBlocking {
            val userSvc = createSamplesInt(true)
            val user = userSvc.findOneByField(User::email, "setup_2@abc.com")!!
            val count = userSvc.count { where(User::level, 7) }
            Assert.assertEquals(1.0, count, 0.0)
            val updated = user.copy(level = 8)
            val isUpdated = userSvc.update(updated)
            Assert.assertTrue(isUpdated)
            val matches = userSvc.findByField(User::level, 8)
            Assert.assertEquals(1, matches.size)
        }
    }


    @Test
    override fun can_count_any() {
        runBlocking {
            val svc = createSamplesInt(false)
            val any1 = svc.any()
            Assert.assertFalse(any1)
            svc.create(User(0, "test_count_any@abc.com", true, 35, 12.34))
            val any2 = svc.any()
            Assert.assertTrue(any2)
        }
    }


    @Test
    override fun can_count_size() {
        runBlocking {
            val svc = createSamplesInt(false)
            val count1 = svc.count()
            Assert.assertTrue(count1 == 0L)
            svc.create(User(0, "test_count_1@abc.com", true, 35, 12.34))
            svc.create(User(0, "test_count_2@abc.com", true, 35, 12.34))
            svc.create(User(0, "test_count_3@abc.com", true, 35, 12.34))

            val count2 = svc.count()
            Assert.assertTrue(count2 == 3L)
        }
    }


    @Test
    override fun can_get_first() {
        runBlocking {
            val svc = createSamplesInt()
            val first = svc.first()
            Assert.assertTrue(first?.email == "setup_1@abc.com")
        }
    }


    @Test
    override fun can_get_last() {
        runBlocking {
            val svc = createSamplesInt()
            val last = svc.last()
            Assert.assertTrue(last?.email == "setup_7@abc.com")
        }
    }


    @Test
    override fun can_get_recent() {
        runBlocking {
            val svc = createSamplesInt()
            val recent = svc.recent(2)
            Assert.assertTrue(recent[0].email == "setup_7@abc.com")
            Assert.assertTrue(recent[1].email == "setup_6@abc.com")
        }
    }


    @Test
    override fun can_get_oldest() {
        runBlocking {
            val svc = createSamplesInt()
            val oldest = svc.oldest(2)
            Assert.assertTrue(oldest[0].email == "setup_1@abc.com")
            Assert.assertTrue(oldest[1].email == "setup_2@abc.com")
        }
    }


    @Test
    override fun can_get_all() {
        runBlocking {
            val svc = createSamplesInt()
            val all = svc.getAll()
            Assert.assertTrue(all.size == 7)
        }
    }


    @Test
    override fun can_find_by_field() {
        runBlocking {
            val svc = createSamplesInt()
            val second = svc.findByField(User::email, Op.Eq, "setup_2@abc.com")
            Assert.assertTrue(second.size == 1)
            Assert.assertTrue(second[0].email == "setup_2@abc.com")
        }
    }


    @Test
    override fun can_get_aggregates() {
        runBlocking {
            val svc = createSamplesInt()
            val count = svc.count()
            val sum = svc.repo().sum(User::level.name) { }
            val avg = svc.repo().avg(User::level.name) { }
            val min = svc.repo().min(User::level.name) { }
            val max = svc.repo().max(User::level.name) { }
            Assert.assertEquals(7, count)
            Assert.assertEquals(28.0, sum, 0.0)
            Assert.assertEquals(4.0, avg, 0.0)
            Assert.assertEquals(1.0, min, 0.0)
            Assert.assertEquals(7.0, max, 0.0)
        }
    }


    @Test
    override fun can_find_by_query() {
        runBlocking {
            val svc = createSamplesInt()
            val matches = svc.find {
                where(User::isActive.name, Op.Eq, false)
                        .and(User::level.name, Op.Gt, 5)
            }
            Assert.assertTrue(matches.size == 2)
            Assert.assertTrue(matches[0].email == "setup_6@abc.com")
            Assert.assertTrue(matches[1].email == "setup_7@abc.com")
        }
    }


    @Test
    override fun can_patch_by_query() {
        runBlocking {
            val svc = createSamplesInt()
            val updated = svc.patch {
                set(User::level,  210).where(User::level.name, Op.Eq, 7)
            }
            Assert.assertEquals(1, updated)
            val item = svc.find { where(User::level, Op.Eq, 210) }.firstOrNull()
            Assert.assertEquals("setup_7@abc.com", item?.email)
            Assert.assertEquals(210, item?.level)
        }
    }


    @Test
    override fun can_get_relation() {
        runBlocking {
            createSamplesInt()
            val memsSvcRaw = entities.getService<Int, Member>()
            val member = memsSvcRaw.getAll().first()
            val memsSvc = EntityServiceRelational<Int, Member>(entities, memsSvcRaw.repo())
            val user = memsSvc.getRelation<User>(member.id, Member::userId, User::class)
            Assert.assertTrue(user != null)
            Assert.assertTrue(user!!.email == "setup_2@abc.com")
        }
    }


    @Test
    override fun can_get_relations() {
        runBlocking {
            val userSvc = createSamplesInt(createMembers = false)
            val groupSvc = entities.getService<Int, Group>()
            val memberSvc = entities.getService<Int, Member>()
            val user1 = userSvc.getAll()[0]
            val user2 = userSvc.getAll()[1]
            val group = groupSvc.getAll().first()

            // Create
            val member1Id = memberSvc.create(Member(0, group.id, user1.id))
            val member2Id = memberSvc.create(Member(0, group.id, user2.id))

            val grpSvc = EntityServiceRelational<Int, Group>(entities, groupSvc.repo())
            val results = grpSvc.getWithRelations<Member>(group.id, Member::class, Member::groupId)
            Assert.assertTrue(results != null)
            Assert.assertTrue(results.first?.name == group.name)
            Assert.assertTrue(results.second.size == 2)
            Assert.assertTrue(results.second.get(0).userId == user1.id)
            Assert.assertTrue(results.second.get(1).userId == user2.id)
        }
    }


    @Test
    override fun can_get_relation_with_object() {
        runBlocking {
            val userSvc = createSamplesInt(createMembers = false)
            val groupSvc = entities.getService<Int, Group>()
            val memberSvc = entities.getService<Int, Member>()
            val user = userSvc.getAll().first()
            val group = groupSvc.getAll().first()

            // Create
            val memberId = memberSvc.create(Member(0, group.id, user.id))

            val memsSvc = EntityServiceRelational<Int, Member>(entities, memberSvc.repo())
            val userAndMember = memsSvc.getWithRelation<User>(memberId, Member::userId, User::class)
            Assert.assertTrue(userAndMember != null)
            Assert.assertTrue(userAndMember!!.first?.groupId == group.id)
            Assert.assertTrue(userAndMember!!.first?.userId == user.id)
            Assert.assertTrue(userAndMember.second!!.email == user.email)
        }
    }




    suspend fun createSamplesInt(setupSamples: Boolean = true, createMembers: Boolean = true): UserServiceInt {
        val userSvc = entities.getService<Int, User>() as UserServiceInt
        val memsSvc = entities.getService<Int, Member>()
        val grpSvc = entities.getService<Int, Group>()

        userSvc.deleteAll()
        memsSvc.deleteAll()
        grpSvc.deleteAll()

        if (setupSamples) {
            // 1. Create first user
            userSvc.create(User(0, "setup_1@abc.com", true, 1, 12.34))

            // 2. Create many
            val userIds = userSvc.saveAll(listOf(
                    User(0, "setup_2@abc.com", true, 2, 12.34),
                    User(0, "setup_3@abc.com", true, 3, 12.34),
                    User(0, "setup_4@abc.com", true, 4, 12.34),
                    User(0, "setup_5@abc.com", false, 5, 12.34),
                    User(0, "setup_6@abc.com", false, 6, 12.34),
                    User(0, "setup_7@abc.com", false, 7, 12.34)
            ))

            // 2. Create groups
            val groupIds = grpSvc.saveAll(listOf(
                    Group(0, "group 1"),
                    Group(0, "group 2"),
                    Group(0, "group 3")
            ))

            // 3. Create many
            if (createMembers) {
                val memberIds = memsSvc.saveAll(listOf(
                        Member(0, groupIds[0].first!!, userIds[0].first!!),
                        Member(0, groupIds[1].first!!, userIds[1].first!!),
                        Member(0, groupIds[2].first!!, userIds[2].first!!)
                ))
            }
        }
        return userSvc
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

/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package test.entities

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kiit.common.data.*
import kiit.entities.features.Relations
import kiit.entities.*
import kiit.entities.features.Counts
import kiit.entities.features.Ordered
import kiit.query.Op
import kiit.query.set
import kiit.query.where
import test.setup.Group
import test.setup.Member
import test.setup.User5
import test.setup.UserNullable

/**
Sql scripts located :
 1. ./install/db/mysql/3.x/
 2. ./install/db/postgres/3.x/
 */
class UserService(repo: EntityRepo<Long, User5>)
    : EntityService<Long, User5>(repo), Ordered<Long, User5>, Counts<Long, User5>




open class Entity_Service_Tests  {

    protected lateinit var entities: Entities
    private val sqlStatements = listOf(
"""create table IF NOT EXISTS `User5` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`uuid` NVARCHAR(50) NOT NULL,
`email` NVARCHAR(100) NOT NULL,
`isActive` BIT NOT NULL,
`level` INTEGER NOT NULL,
`salary` DOUBLE NOT NULL,
`createdat` DATETIME NOT NULL,
`createdby` BIGINT NOT NULL,
`updatedat` DATETIME NOT NULL,
`updatedby` BIGINT NOT NULL
);""",

""" create table IF NOT EXISTS `UserNullable` (
`id`        BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`uuid`      NVARCHAR(50) NOT NULL,
`email`     NVARCHAR(100),
`isActive`  BIT ,
`level`     INTEGER,
`salary`    DOUBLE,
`createdAt` DATETIME,
`createdBy` BIGINT,
`updatedAt` DATETIME,
`updatedBy` BIGINT
);""",

"""create table IF NOT EXISTS `Member` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`groupid` BIGINT NOT NULL,
`userid` BIGINT NOT NULL
);""",

"""create table IF NOT EXISTS `Group` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`name` NVARCHAR(30) NOT NULL
);"""
    )

    @Before
    fun setup() {
//        entities = Entities({ con -> Db(con) }, Connections(DbConString("", "", "", "")))
        entities = EntitySetup.realDb() //Vendor.MySql, "KIIT")
        val db = entities.getDb()
        sqlStatements.map{ sql ->
            db.execute(sql)
        }
        //entities.register<Long, SampleEntityImmutable>(LongId { s -> s.id}, "sample_entity", Vendor.MySql) { repo -> EntityService(repo) }
        entities.register<Long, User5>(EntityLongId(), vendor = Vendor.MySql) { repo -> UserService(repo) }
        entities.register<Long, Member>(EntityLongId(), vendor = Vendor.MySql) { repo -> EntityService(repo) }
        entities.register<Long, Group>(EntityLongId(), vendor = Vendor.MySql) { repo -> EntityService(repo) }
        entities.register<Long, UserNullable>(EntityLongId(), vendor = Vendor.MySql) { repo -> EntityService(repo) }
    }


    @Test
    fun can_create_an_item() {
//        runBlocking {
//            val userSvc = createSamples(false)
//            val id = userSvc.create(User5(0, "test_create@abc.com", true, 35, 12.34))
//            val User5 = userSvc.getById(id)
//            Assert.assertTrue(User5 != null)
//            Assert.assertTrue(User5?.email == "test_create@abc.com")
//            Assert.assertTrue(User5?.isActive == true)
//            Assert.assertTrue(User5?.level == 35)
//            Assert.assertTrue(User5?.salary == 12.34)
//        }
    }


    @Test
    fun can_use_model_with_nullable_fields() {
        runBlocking {
            val userSvc = entities.getService<Long, UserNullable>()
            val created = UserNullable()
            val id = userSvc.create(created)
            val user = userSvc.getById(id)
            Assert.assertTrue(user != null)
            Assert.assertNull(user?.email)
            Assert.assertNull(user?.isActive)
            Assert.assertNull(user?.level)
            Assert.assertNull(user?.salary)
            Assert.assertNull(user?.createdAt)
            Assert.assertNull(user?.createdBy)
            Assert.assertNull(user?.updatedAt)
            Assert.assertNull(user?.updatedBy)
        }
    }


    @Test
    fun can_update_an_item() {
        val lc = kiit.common.Types.JLongClass
        val kc = Long::class.java
        val isEqual = lc == kc
        println(isEqual)
        runBlocking {
            val userSvc = createSamples(true)
            val user = userSvc.findOneByField(User5::email, "setup_2@abc.com")!!
            val count = userSvc.count { where(User5::level, 7) }
            Assert.assertEquals(1.0, count, 0.0)
            val updated = user.copy(level = 8)
            val isUpdated = userSvc.update(updated)
            Assert.assertTrue(isUpdated)
            val matches = userSvc.findByField(User5::level, 8)
            Assert.assertEquals(1, matches.size)
        }
    }


    @Test
    fun can_get_relation() {
        runBlocking {
            createSamples()
            val memsSvcRaw = entities.getService<Long, Member>()
            val member = memsSvcRaw.getAll().first()
            val memsSvc = EntityServiceRelational<Long, Member>(entities, memsSvcRaw.repo())
            val user = memsSvc.getRelation<User5>(member.id, Member::userId, User5::class)
            Assert.assertTrue(user != null)
            Assert.assertTrue(user!!.email == "setup_2@abc.com")
        }
    }


    @Test
    fun can_get_relations() {
        runBlocking {
            val userSvc = createSamples(createMembers = false)
            val groupSvc = entities.getService<Long, Group>()
            val memberSvc = entities.getService<Long, Member>()
            val user1 = userSvc.getAll()[0]
            val user2 = userSvc.getAll()[1]
            val group = groupSvc.getAll().first()

            // Create
            val member1Id = memberSvc.create(Member(0, group.id, user1.id))
            val member2Id = memberSvc.create(Member(0, group.id, user2.id))

            val grpSvc = EntityServiceRelational<Long, Group>(entities, groupSvc.repo())
            val results = grpSvc.getWithRelations<Member>(group.id, Member::class, Member::groupId)
            Assert.assertTrue(results != null)
            Assert.assertTrue(results.first?.name == group.name)
            Assert.assertTrue(results.second.size == 2)
            Assert.assertTrue(results.second.get(0).userId == user1.id)
            Assert.assertTrue(results.second.get(1).userId == user2.id)
        }
    }


    @Test
    fun can_get_relation_with_object() {
        runBlocking {
            val userSvc = createSamples(createMembers = false)
            val groupSvc = entities.getService<Long, Group>()
            val memberSvc = entities.getService<Long, Member>()
            val user = userSvc.getAll().first()
            val group = groupSvc.getAll().first()

            // Create
            val memberId = memberSvc.create(Member(0, group.id, user.id))

            val memsSvc = EntityServiceRelational<Long, Member>(entities, memberSvc.repo())
            val userAndMember = memsSvc.getWithRelation<User5>(memberId, Member::userId, User5::class)
            Assert.assertTrue(userAndMember != null)
            Assert.assertTrue(userAndMember!!.first?.groupId == group.id)
            Assert.assertTrue(userAndMember!!.first?.userId == user.id)
            Assert.assertTrue(userAndMember.second!!.email == user.email)
        }
    }


    class EntityServiceRelational<TId, T>(val entities: Entities, repo: EntityRepo<TId, T>)
        : EntityService<TId, T>(repo), Relations<TId, T>
            where TId : Comparable<TId>, T : Entity<TId> {

        override fun entities(): Entities {
            return entities
        }
    }


    protected open suspend fun createSamples(setupSamples: Boolean = true, createMembers: Boolean = true): UserService {
        val userSvc = entities.getService<Long, User5>() as UserService
        val memsSvc = entities.getService<Long, Member>()
        val grpSvc = entities.getService<Long, Group>()

        userSvc.deleteAll()
        memsSvc.deleteAll()
        grpSvc.deleteAll()

//        if (setupSamples) {
//            // 1. Create first user
//            userSvc.create(User5(0, "setup_1@abc.com", true, 1, 12.34))
//
//            // 2. Create many
//            val userIds = userSvc.saveAll(listOf(
//                    User5(0, "setup_2@abc.com", true, 2, 12.34),
//                    User5(0, "setup_3@abc.com", true, 3, 12.34),
//                    User5(0, "setup_4@abc.com", true, 4, 12.34),
//                    User5(0, "setup_5@abc.com", false, 5, 12.34),
//                    User5(0, "setup_6@abc.com", false, 6, 12.34),
//                    User5(0, "setup_7@abc.com", false, 7, 12.34)
//            ))
//
//            // 2. Create groups
//            val groupIds = grpSvc.saveAll(listOf(
//                    Group(0, "group 1"),
//                    Group(0, "group 2"),
//                    Group(0, "group 3")
//            ))
//
//            // 3. Create many
//            if (createMembers) {
//                val memberIds = memsSvc.saveAll(listOf(
//                        Member(0, groupIds[0].first!!, userIds[0].first!!),
//                        Member(0, groupIds[1].first!!, userIds[1].first!!),
//                        Member(0, groupIds[2].first!!, userIds[2].first!!)
//                ))
//            }
//        }
        return userSvc
    }

}

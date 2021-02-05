/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package test.entities

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import slatekit.common.data.*
import slatekit.entities.features.Relations
import slatekit.entities.*
import slatekit.entities.features.Counts
import slatekit.entities.features.Ordered
import slatekit.query.Op
import slatekit.query.set
import slatekit.query.where
import test.setup.Group
import test.setup.Member
import test.setup.User5

/**
create table `User5` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`email` NVARCHAR(100) NOT NULL,
`isactive` BIT NOT NULL,
`age` INTEGER NOT NULL,
`salary` DOUBLE NOT NULL,
`createdat` DATETIME NOT NULL,
`createdby` BIGINT NOT NULL,
`updatedat` DATETIME NOT NULL,
`updatedby` BIGINT NOT NULL,
`uniqueid` NVARCHAR(50) NOT NULL
);

create table `Member` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`groupid` BIGINT NOT NULL,
`userid` BIGINT NOT NULL
);

create table `Group` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`name` NVARCHAR(30) NOT NULL
);
 */
class UserService(repo: EntityRepo<Long, User5>)
    : EntityService<Long, User5>(repo), Ordered<Long, User5>, Counts<Long, User5>


class Data_04_Entity_Service {

    private lateinit var entities: Entities


    @Before
    fun setup() {
//        entities = Entities({ con -> Db(con) }, Connections(DbConString("", "", "", "")))
        entities = EntitySetup.realDb()
        //entities.register<Long, SampleEntityImmutable>(LongId { s -> s.id}, "sample_entity", Vendor.MySql) { repo -> EntityService(repo) }
        entities.register<Long, User5>(EntityLongId(), vendor = Vendor.MySql) { repo -> UserService(repo) }
        entities.register<Long, Member>(EntityLongId(), vendor = Vendor.MySql) { repo -> EntityService(repo) }
        entities.register<Long, Group>(EntityLongId(), vendor = Vendor.MySql) { repo -> EntityService(repo) }
    }


    private fun getSvc(): EntityService<Long, SampleEntityImmutable> = entities.getService<Long, SampleEntityImmutable>()
    private fun getUsrs(): EntityService<Long, User5> = entities.getService<Long, User5>()


    private suspend fun createSamples(setupSamples: Boolean = true, createMembers: Boolean = true): UserService {
        val userSvc = entities.getService<Long, User5>() as UserService
        val memsSvc = entities.getService<Long, Member>()
        val grpSvc = entities.getService<Long, Group>()

        userSvc.deleteAll()
        memsSvc.deleteAll()
        grpSvc.deleteAll()

        if (setupSamples) {
            // 1. Create first user
            userSvc.create(User5(0, "setup_1@abc.com", true, 1, 12.34))

            // 2. Create many
            val userIds = userSvc.saveAll(listOf(
                    User5(0, "setup_2@abc.com", true, 2, 12.34),
                    User5(0, "setup_3@abc.com", true, 3, 12.34),
                    User5(0, "setup_4@abc.com", true, 4, 12.34),
                    User5(0, "setup_5@abc.com", false, 5, 12.34),
                    User5(0, "setup_6@abc.com", false, 6, 12.34),
                    User5(0, "setup_7@abc.com", false, 7, 12.34)
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


    @Test
    fun can_create_an_item() {
        runBlocking {
            val userSvc = createSamples(false)
            val id = userSvc.create(User5(0, "test_create@abc.com", true, 35, 12.34))
            val User5 = userSvc.getById(id)
            Assert.assertTrue(User5 != null)
            Assert.assertTrue(User5?.email == "test_create@abc.com")
            Assert.assertTrue(User5?.isActive == true)
            Assert.assertTrue(User5?.level == 35)
            Assert.assertTrue(User5?.salary == 12.34)
        }
    }


    @Test
    fun can_count_any() {
        runBlocking {
            val svc = createSamples(false)
            val any1 = svc.any()
            Assert.assertFalse(any1)
            svc.create(User5(0, "test_count_any@abc.com", true, 35, 12.34))
            val any2 = svc.any()
            Assert.assertTrue(any2)
        }
    }


    @Test
    fun can_count_size() {
        runBlocking {
            val svc = createSamples(false)
            val count1 = svc.count()
            Assert.assertTrue(count1 == 0L)
            svc.create(User5(0, "test_count_1@abc.com", true, 35, 12.34))
            svc.create(User5(0, "test_count_2@abc.com", true, 35, 12.34))
            svc.create(User5(0, "test_count_3@abc.com", true, 35, 12.34))

            val count2 = svc.count()
            Assert.assertTrue(count2 == 3L)
        }
    }


    @Test
    fun can_get_first() {
        runBlocking {
            val svc = createSamples()
            val first = svc.first()
            Assert.assertTrue(first?.email == "setup_1@abc.com")
        }
    }


    @Test
    fun can_get_last() {
        runBlocking {
            val svc = createSamples()
            val last = svc.last()
            Assert.assertTrue(last?.email == "setup_7@abc.com")
        }
    }


    @Test
    fun can_get_recent() {
        runBlocking {
            val svc = createSamples()
            val recent = svc.recent(2)
            Assert.assertTrue(recent[0].email == "setup_7@abc.com")
            Assert.assertTrue(recent[1].email == "setup_6@abc.com")
        }
    }


    @Test
    fun can_get_oldest() {
        runBlocking {
            val svc = createSamples()
            val oldest = svc.oldest(2)
            Assert.assertTrue(oldest[0].email == "setup_1@abc.com")
            Assert.assertTrue(oldest[1].email == "setup_2@abc.com")
        }
    }


    @Test
    fun can_get_all() {
        runBlocking {
            val svc = createSamples()
            val all = svc.getAll()
            Assert.assertTrue(all.size == 7)
        }
    }


    @Test
    fun can_find_by_field() {
        runBlocking {
            val svc = createSamples()
            val second = svc.findByField(User5::email, Op.Eq, "setup_2@abc.com")
            Assert.assertTrue(second.size == 1)
            Assert.assertTrue(second[0].email == "setup_2@abc.com")
        }
    }


    @Test
    fun can_get_aggregates() {
        runBlocking {
            val svc = createSamples()
            val count = svc.count()
            val sum = svc.repo().sum(User5::level.name) { }
            val avg = svc.repo().avg(User5::level.name) { }
            val min = svc.repo().min(User5::level.name) { }
            val max = svc.repo().max(User5::level.name) { }
            Assert.assertEquals(7, count)
            Assert.assertEquals(28.0, sum, 0.0)
            Assert.assertEquals(4.0, avg, 0.0)
            Assert.assertEquals(1.0, min, 0.0)
            Assert.assertEquals(7.0, max, 0.0)
        }
    }


    @Test
    fun can_find_by_query() {
        runBlocking {
            val svc = createSamples()
            val matches = svc.find {
                where(User5::isActive.name, Op.Eq, false)
                        .and(User5::level.name, Op.Gt, 5)
            }
            Assert.assertTrue(matches.size == 2)
            Assert.assertTrue(matches[0].email == "setup_6@abc.com")
            Assert.assertTrue(matches[1].email == "setup_7@abc.com")
        }
    }


    @Test
    fun can_patch_by_query() {
        runBlocking {
            val svc = createSamples()
            val updated = svc.patch {
                set(User5::level,  210).where(User5::level.name, Op.Eq, 7)
            }
            Assert.assertEquals(1, updated)
            val item = svc.find { where(User5::level, Op.Eq, 210) }.firstOrNull()
            Assert.assertEquals("setup_7@abc.com", item?.email)
            Assert.assertEquals(210, item?.level)
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

}

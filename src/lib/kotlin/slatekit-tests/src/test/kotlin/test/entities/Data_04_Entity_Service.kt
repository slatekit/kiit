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

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import slatekit.common.data.*
import slatekit.db.Db
import slatekit.entities.features.Relations
import slatekit.entities.*
import test.setup.Group
import test.setup.Member
import test.setup.User5


class Data_04_Entity_Service {

    private lateinit var entities:Entities



    @Before fun setup(){
        entities = Entities({ con -> Db(con) }, Connections(DbConString("", "", "", "")))
        entities.register<Long, User5>(EntityLongId() , vendor = Vendor.Memory) { repo -> EntityService(repo) }
        entities.register<Long, Member>(EntityLongId(), vendor = Vendor.Memory) { repo -> EntityService(repo) }
        entities.register<Long, Group>(EntityLongId() , vendor = Vendor.Memory) { repo -> EntityService(repo) }
    }


    private fun getUserService(setupSamples:Boolean): EntityService<Long, User5> {
        val userSvc = entities.getService<Long, User5>()
        val memsSvc = entities.getService<Long, Member>()
        val grpSvc = entities.getService<Long, Group>()

        if(setupSamples) {
            // 1. Create first user
            userSvc.create(User5(0, "jdoe1@abc.com", true, 35, 12.34))

            // 2. Create many
            userSvc.saveAll(listOf(
                    User5(0, "jdoe2@abc.com", true , 35, 12.34),
                    User5(0, "jdoe3@abc.com", true , 35, 12.34),
                    User5(0, "jdoe4@abc.com", true , 35, 12.34),
                    User5(0, "jdoe5@abc.com", false, 40, 12.34),
                    User5(0, "jdoe6@abc.com", false, 40, 12.34),
                    User5(0, "jdoe7@abc.com", false, 42, 12.34)
            ))

            // 3. Create many
            memsSvc.saveAll(listOf(
                    Member(0, 2, 1),
                    Member(0, 2, 2),
                    Member(0, 3, 3)
            ))

            // 3. Create many
            grpSvc.saveAll(listOf(
                    Group(0, "group 1"),
                    Group(0, "group 2"),
                    Group(0, "group 3")
            ))
        }
        return userSvc
    }


    @Test
    fun can_create_an_item() {
        val userSvc = getUserService(true)
        userSvc.create(User5(0, "jdoe5@abc.com", true, 35, 12.34))
        val User5 = userSvc.getById(5)!!
        Assert.assertTrue(User5 != null)
        Assert.assertTrue(User5.email == "jdoe5@abc.com")
        //Assert.assertTrue( User5.get.uuid != "")
    }


    @Test fun can_check_any() {
        val svc = getUserService(true)
        val any = svc.any()
        Assert.assertTrue(any)
    }


    @Test fun can_check_count() {
        val svc = getUserService(true)
        val count = svc.count()
        Assert.assertTrue(count == 7L)
    }


    @Test fun can_get_first() {
        val svc = getUserService(true)
        val first = svc.first()
        Assert.assertTrue(first?.email == "jdoe1@abc.com")
    }


    @Test fun can_get_last() {
        val svc = getUserService(true)
        val last = svc.last()
        Assert.assertTrue(last?.email == "jdoe7@abc.com")
    }


    @Test fun can_get_recent() {
        val svc = getUserService(true)
        val recent = svc.recent(2)
        Assert.assertTrue(recent[0].email == "jdoe7@abc.com")
        Assert.assertTrue(recent[1].email == "jdoe6@abc.com")
    }


    @Test fun can_get_oldest() {
        val svc = getUserService(true)
        val oldest = svc.oldest(2)
        Assert.assertTrue(oldest[0].email == "jdoe1@abc.com")
        Assert.assertTrue(oldest[1].email == "jdoe2@abc.com")
    }


    @Test fun can_get_by_id() {
        val userSvc = getUserService(true)
        val memsSvc = entities.getSvc<Long, Member>(Member::class)
        val first = userSvc.first()
        val firstById = userSvc.get(first?.id ?: 1)
        val memberById = memsSvc.get(first?.id ?: 1)
        Assert.assertTrue(firstById?.email == "jdoe1@abc.com")
        Assert.assertTrue(memberById?.groupId == 2L)
        Assert.assertTrue(memberById?.userId == 1L)
    }


    @Test fun can_find_by_field() {
        val svc = getUserService(true)
        val second = svc.findByField(User5::email, Op.Eq, "jdoe2@abc.com")
        Assert.assertTrue(second.size == 1)
        Assert.assertTrue(second[0].email == "jdoe2@abc.com")
    }


    @Test fun can_find_by_fields() {
        val svc = getUserService(true)
        val matches = svc.findByFilters(listOf(
                Filter(User5::isActive.name, Op.Eq,false),
                Filter(User5::age.name, Op.Eq,40)
        ), Logical.And)
        Assert.assertTrue(matches.size == 2)
        Assert.assertTrue(matches[0].email == "jdoe5@abc.com")
    }


    @Test fun can_get_all() {
        val svc = getUserService(true)
        val all = svc.getAll()
        Assert.assertTrue(all.size == 7)
    }


    @Test fun can_get_relation() {
        val userSvc = getUserService(true)
        val memsSvcRaw = entities.getSvc<Long, Member>(Member::class)
        val memsSvc = EntityServiceRelational<Long, Member>(entities, memsSvcRaw.repo())
        val user = memsSvc.getRelation<User5>(1, Member::userId, User5::class)
        Assert.assertTrue( user != null)
        Assert.assertTrue( user!!.email == "jdoe1@abc.com")
    }


    @Test fun can_get_relation_with_object() {
        val userSvc = getUserService(true)
        val memsSvcRaw = entities.getSvc<Long, Member>(Member::class)
        val memsSvc = EntityServiceRelational<Long, Member>(entities, memsSvcRaw.repo())
        val userAndMember = memsSvc.getWithRelation<User5>(2, Member::userId, User5::class)
        Assert.assertTrue( userAndMember != null)
        Assert.assertTrue(userAndMember!!.first?.groupId == 2L)
        Assert.assertTrue(userAndMember!!.first?.userId == 2L)
        Assert.assertTrue( userAndMember.second!!.email == "jdoe2@abc.com")
    }


    @Test fun can_get_relations() {
        val userSvc = getUserService(true)
        val grpSvcRaw = entities.getSvc<Long, Group>(Group::class)
        val grpSvc = EntityServiceRelational<Long, Group>(entities, grpSvcRaw.repo())
        val results = grpSvc.getWithRelations<Member>(2, Member::class, Member::groupId)
        Assert.assertTrue(results != null)
        Assert.assertTrue(results.first?.name == "group 2")
        Assert.assertTrue(results.second.size == 2)
        Assert.assertTrue(results.second.get(0).userId == 1L)
        Assert.assertTrue(results.second.get(1).userId == 2L)
    }


    class EntityServiceRelational<TId, T>(val entities:Entities, repo: EntityRepo<TId, T>)
        : EntityService<TId, T>(repo), Relations<TId, T>
            where TId:Comparable<TId>, T: Entity<TId> {

        override fun entities(): Entities {
            return entities
        }

    }

}

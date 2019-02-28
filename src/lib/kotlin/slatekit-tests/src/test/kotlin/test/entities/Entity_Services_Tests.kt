/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package test.entities

import org.junit.Before
import org.junit.Test
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.entities.core.*
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.meta.models.ModelMapper
import test.setup.Group
import test.setup.Member
import test.setup.User5


class Entity_Services_Tests {

    private var entities = Entities<EntityInfo>()


    @Before fun setup(){
        entities = Entities(DbLookup(DbConString("", "", "", "")))
        entities.register<Long, User5>(User5::class)
        entities.register<Long, Member>(Member::class)
        entities.register<Long, Group>(Group::class)
    }


    private fun getUserService(setupSamples:Boolean): EntityService<Long, User5> {
        val userSvc = entities.getSvc<Long, User5>(User5::class)
        val memsSvc = entities.getSvc<Long, Member>(Member::class)
        val grpSvc = entities.getSvc<Long, Group>(Group::class)

        if(setupSamples) {
            // 1. Create first user
            userSvc.create(User5(0, "jdoe1@abc.com", true, 35, 12.34))

            // 2. Create many
            userSvc.saveAll(listOf(
                    User5(0, "jdoe2@abc.com", true, 35, 12.34),
                    User5(0, "jdoe3@abc.com", true, 35, 12.34),
                    User5(0, "jdoe4@abc.com", true, 35, 12.34)
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
        val User5 = userSvc.get(5)!!
        assert(User5 != null)
        assert(User5.email == "jdoe5@abc.com")
        //assert( User5.get.uuid != "")
    }


    @Test fun can_check_any() {
        val svc = getUserService(true)
        val any = svc.any()
        assert(any)
    }


    @Test fun can_check_count() {
        val svc = getUserService(true)
        val count = svc.count()
        assert(count == 4L)
    }


    @Test fun can_get_first() {
        val svc = getUserService(true)
        val first = svc.first()
        assert(first?.email == "jdoe1@abc.com")
    }


    @Test fun can_get_last() {
        val svc = getUserService(true)
        val last = svc.last()
        assert(last?.email == "jdoe4@abc.com")
    }


    @Test fun can_get_recent() {
        val svc = getUserService(true)
        val recent = svc.recent(2)
        assert(recent[0].email == "jdoe4@abc.com")
        assert(recent[1].email == "jdoe3@abc.com")
    }


    @Test fun can_get_oldest() {
        val svc = getUserService(true)
        val oldest = svc.oldest(2)
        assert(oldest[0].email == "jdoe1@abc.com")
        assert(oldest[1].email == "jdoe2@abc.com")
    }


    @Test fun can_get_by_id() {
        val userSvc = getUserService(true)
        val memsSvc = entities.getSvc<Long, Member>(Member::class)
        val first = userSvc.first()
        val firstById = userSvc.get(first?.id ?: 1)
        val memberById = memsSvc.get(first?.id ?: 1)
        assert(firstById?.email == "jdoe1@abc.com")
        assert(memberById?.groupId == 2L)
        assert(memberById?.userId == 1L)
    }


    @Test fun can_find_by_field() {
        val svc = getUserService(true)
        val second = svc.findByField(User5::email, "jdoe2@abc.com")
        assert(second.size == 1)
        assert(second[0].email == "jdoe2@abc.com")
    }


    @Test fun can_get_all() {
        val svc = getUserService(true)
        val all = svc.getAll()
        assert(all.size == 4)
    }


    @Test fun can_get_relation() {
        val userSvc = getUserService(true)
        val memsSvc = entities.getSvc<Long, Member>(Member::class)
        val user = memsSvc.getRelation<User5>(1, Member::userId, User5::class)
        assert( user != null)
        assert( user!!.email == "jdoe1@abc.com")
    }


    @Test fun can_get_relation_with_object() {
        val userSvc = getUserService(true)
        val memsSvc = entities.getSvc<Long, Member>(Member::class)
        val userAndMember = memsSvc.getWithRelation<User5>(2, Member::userId, User5::class)
        assert( userAndMember != null)
        assert(userAndMember!!.first?.groupId == 2L)
        assert(userAndMember!!.first?.userId == 2L)
        assert( userAndMember.second!!.email == "jdoe2@abc.com")
    }


    @Test fun can_get_relations() {
        val userSvc = getUserService(true)
        val grpSvc = entities.getSvc<Long, Group>(Group::class)

        val results = grpSvc.getWithRelations<Member>(2, Member::class, Member::groupId)
        assert(results != null)
        assert(results.first?.name == "group 2")
        assert(results.second.size == 2)
        assert(results.second.get(0).userId == 1L)
        assert(results.second.get(1).userId == 2L)
    }

}

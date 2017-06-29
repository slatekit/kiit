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

package slate.test

import org.junit.Test
import slate.common.Random
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.db.DbTypeMemory
import slatekit.common.mapper.Mapper
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityMapper
import slatekit.entities.core.EntityRepo
import slatekit.entities.core.EntityService
import test.common.Phone
import test.common.User5


class EntityTests {


      @Test fun can_register_the_entity() {
        val ent =  Entities()
        ent.register<User5>(isSqlRepo= false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo= false, entityType = Phone::class)

        val ents =  ent.getEntities()

        assert(ents.size == 2)
        assert( ent.getEntities()[0].dbType == DbTypeMemory)
        assert( !ent.getEntities()[0].isSqlRepo )
        assert( ent.getEntities()[0].entityType == User5::class)
        assert( ent.getEntities()[0].entityTypeName == "test.common.User5")
      }


      @Test fun can_get_service() {
        val ent =  Entities()
        ent.register<User5>(isSqlRepo= false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo= false, entityType = Phone::class)

        assert( ent.getSvc<User5>(User5::class) is EntityService<User5> )
        assert( ent.getSvc<Phone>(Phone::class) is EntityService<Phone>)
      }


      @Test fun can_get_service_with_shards() {
        val ent =  Entities()
        ent.register<User5>(isSqlRepo= false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo= false, entityType = Phone::class)

        assert( ent.getSvc<User5>(User5::class, "", "") is EntityService<User5>)
        assert( ent.getSvc<Phone>(Phone::class, "", "") is EntityService<Phone>)
      }


      @Test fun can_get_repo() {
        val ent =  Entities()
        ent.register<User5>(isSqlRepo= false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo= false, entityType = Phone::class)

        assert( ent.getRepo<User5>(User5::class) is EntityRepo<User5>)
        assert( ent.getRepo<Phone>(Phone::class) is EntityRepo<Phone>)
      }


      @Test fun can_get_repo_with_shards() {
        val ent =  Entities()
        ent.register<User5>(isSqlRepo= false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo= false, entityType = Phone::class)

        assert( ent.getRepo<User5>(User5::class, "", "") is EntityRepo<User5>)
        assert( ent.getRepo<Phone>(Phone::class, "", "") is EntityRepo<Phone>)
      }


      @Test fun can_create_an_item() {
        val svc = service()

        svc.create( User5(0, "jdoe@abc.com", true, 35, 12.34) )
        val User5 = svc.get(1)!!
        assert( User5 != null )
        assert( User5.email == "jdoe@abc.com" )
        //assert( User5.get.uniqueId != "")
      }


    @Test fun can_perform_operations():Unit {
        val svc = service()

        // 1. Create first user
        svc.create(User5(0, "jdoe1@abc.com", true, 35, 12.34))

        // 2. Create many
        svc.saveAll(listOf(
                User5(0, "jdoe2@abc.com", true, 35, 12.34),
                User5(0, "jdoe3@abc.com", true, 35, 12.34),
                User5(0, "jdoe4@abc.com", true, 35, 12.34)
        ))

        // 3. Any ?
        val any = svc.any()
        assert( any )

        // 4. Count
        val count = svc.count()
        assert( count == 4L )

        // 5. First
        val first = svc.first()
        assert( first?.email == "jdoe1@abc.com")

        // 6. Last
        val last = svc.last()
        assert( last?.email == "jdoe4@abc.com")

        // 7. Recent / newest
        val recent = svc.recent(2)
        assert( recent[0].email == "jdoe4@abc.com")
        assert( recent[1].email == "jdoe3@abc.com")

        // 8. Oldest
        val oldest = svc.oldest(2)
        assert( oldest[0].email == "jdoe1@abc.com")
        assert( oldest[1].email == "jdoe2@abc.com")

        // 9. Get by id
        val firstById = svc.get(first?.id ?: 1)
        assert( firstById?.email == "jdoe1@abc.com")

        // 10. Get by field
        val second = svc.findBy(User5::email, "jdoe2@abc.com")
        assert(second.size == 1)
        assert(second[0].email == "jdoe2@abc.com")

        // 11. Find by field
        val all = svc.getAll()
        assert(all.size == 4)
    }


/*
      @Test fun can_update_an_item() {
        val svc = service()

        svc.create( User5(0, "john", "doe"))
        var User5 = svc.get(1)
        val originalUniqueId = User5.uniqueId
        val originalCreateDate = User5.createdAt.toStringYYYYMMDDHHmmss()
        //val originalUpdateDate = User5.get.updatedAt.toStringYYYYMMDDHHmmss()

        svc.update(User5!!)
        User5 = svc.get(1)!!

        assert( user != null )
        assert( user.firstName == "clark" )
        assert( user.lastName == "kent" )
        assert( user.createdAt.toStringYYYYMMDDHHmmss == originalCreateDate)
        assert( user.uniqueId == originalUniqueId)

      }


      @Test fun can_get_all_items() {
        val svc = service()

        svc.create( User(0, "clark", "kent"))
        svc.create( User(0, "bruce", "wayne"))
        val users = svc.getAll()

        assert( users(0).id != 0L )
        assert( users(1).id != 0L )
        assert( users.size == 2 )
      }

      @Test fun can_delete_an_item() {

        val svc = service()

        svc.create( User(0, "clark", "kent"))
        svc.create( User(0, "bruce", "wayne"))
        val user = svc.get(2)
        svc.delete(user)
        val users = svc.getAll()

        assert( users.size == 1 )
      }
*/

  private fun service(): EntityService<User5> {
    // 1. Setup the mapper
    val model = Mapper.loadSchema(User5::class)
    val mapper =  EntityMapper(model)

    // 2. Setup repo
    val repo =  EntityRepoInMemory<User5>(User5::class)

    // 3. Setup service
    val svc =  EntityService<User5>(repo)
    return svc
  }
}

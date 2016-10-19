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

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.entities.core.{EntityMapper, EntityService}
import slate.entities.repos.EntityRepoInMemory
import slate.test.common.User
import scala.reflect.runtime.universe.typeOf

class EntityTests  extends FunSpec with BeforeAndAfter with BeforeAndAfterAll {

  describe( "Entities" ) {


    describe( "Field Management" ) {

      it("can apply field data") {
        val user = new User()
        val svc = new EntityService[User]()
        svc.applyFieldData(1, Some(user))
        assert(user.createdAt.toString != "")
        assert(user.updatedAt.toString != "")
        assert(user.uniqueId != "")
      }
    }


    describe( "Service" ) {

      it("can create an item") {
        val svc = service()

        svc.create(new User().init("john", "doe"))
        val user = svc.get(1)
        assert( user.isDefined )
        assert( user.get.firstName == "john" )
        assert( user.get.lastName == "doe" )
        assert( user.get.createdAt.toString != "")
        assert( user.get.updatedAt.toString != "")
        assert( user.get.uniqueId != "")
      }


      it("can update an item") {
        val svc = service()

        svc.create(new User().init("john", "doe"))
        var user = svc.get(1)
        val originalUniqueId = user.get.uniqueId
        val originalCreateDate = user.get.createdAt.toStringYYYYMMDDHHmmss()
        val originalUpdateDate = user.get.updatedAt.toStringYYYYMMDDHHmmss()
        user.get.init("clark", "kent")
        Thread.sleep(2000)
        svc.update(user.get)
        user = svc.get(1)

        assert( user.isDefined )
        assert( user.get.firstName == "clark" )
        assert( user.get.lastName == "kent" )
        assert( user.get.createdAt.toStringYYYYMMDDHHmmss == originalCreateDate)
        assert( user.get.updatedAt.toStringYYYYMMDDHHmmss != originalUpdateDate)
        assert( user.get.uniqueId == originalUniqueId)

      }


      it("can get all items") {
        val svc = service()

        svc.create(new User().init("clark", "kent"))
        svc.create(new User().init("bruce", "wayne"))
        val users = svc.getAll()
        assert( users.length == 2 )
      }


      it("can delete an item") {

        val svc = service()

        svc.create(new User().init("clark", "kent"))
        svc.create(new User().init("bruce", "wayne"))
        val user = svc.get(2)
        svc.delete(user)
        val users = svc.getAll()

        assert( users.length == 1 )
      }
    }
  }


  private def service(): EntityService[User] = {
    // 1. Setup the mapper
    val mapper = new EntityMapper(null)
    mapper.loadSchema(new User(), typeOf[User])

    // 2. Setup repo
    val repo = new EntityRepoInMemory[User](typeOf[User])
    repo.setMapper(mapper)

    // 3. Setup service
    val svc = new EntityService[User](repo)
    svc
  }
}

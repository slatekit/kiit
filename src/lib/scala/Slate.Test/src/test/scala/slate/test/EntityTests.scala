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
import slate.entities.core.{IEntityRepo, EntityRepo, Entities, EntityService}
import slate.entities.repos.EntityRepoInMemory
import slate.test.common.{Phone, User}
import scala.reflect.runtime.universe._

class EntityTests  extends FunSpec with BeforeAndAfter with BeforeAndAfterAll {

  describe( "Entities" ) {


    describe( "Field Management" ) {

      /*
      it("can apply field data") {
        val user = new User()
        val svc = new EntityService[User](new EntityRepoInMemory[User](typeOf[User], typeOf[Long], null))
        svc.applyFieldData(1, Some(user))
        assert(user.createdAt.toString != "")
        assert(user.updatedAt.toString != "")
        assert(user.uniqueId != "")
      }
      */
    }


    describe( "Registration" ) {
      it("can register the entity") {
        val ent = new Entities()
        ent.register[User](isSqlRepo= false, entityType = typeOf[User])
        ent.register[Phone](isSqlRepo= false, entityType = typeOf[Phone])
      }


      it("can get service") {
        val ent = new Entities()
        ent.register[User](isSqlRepo= false, entityType = typeOf[User])
        ent.register[Phone](isSqlRepo= false, entityType = typeOf[Phone])

        assert( ent.getSvc[User]().isInstanceOf[EntityService[User]])
        assert( ent.getSvc[Phone]().isInstanceOf[EntityService[Phone]])
      }


      it("can get service with shards") {
        val ent = new Entities()
        ent.register[User](isSqlRepo= false, entityType = typeOf[User])
        ent.register[Phone](isSqlRepo= false, entityType = typeOf[Phone])

        assert( ent.getSvc[User]("", "").isInstanceOf[EntityService[User]])
        assert( ent.getSvc[Phone]("", "").isInstanceOf[EntityService[Phone]])
      }


      it("can get repo") {
        val ent = new Entities()
        ent.register[User](isSqlRepo= false, entityType = typeOf[User])
        ent.register[Phone](isSqlRepo= false, entityType = typeOf[Phone])

        assert( ent.getRepo[User]().isInstanceOf[EntityRepo[User]])
        assert( ent.getRepo[Phone]().isInstanceOf[EntityRepo[Phone]])
      }


      it("can get repo with shards") {
        val ent = new Entities()
        ent.register[User](isSqlRepo= false, entityType = typeOf[User])
        ent.register[Phone](isSqlRepo= false, entityType = typeOf[Phone])

        assert( ent.getRepo[User]("", "").isInstanceOf[EntityRepo[User]])
        assert( ent.getRepo[Phone]("", "").isInstanceOf[EntityRepo[Phone]])
      }
    }


    describe( "Service" ) {

      it("can create an item") {
        val svc = service()

        svc.create(new User(0).init("john", "doe"))
        val user = svc.get(1)
        assert( user.isDefined )
        assert( user.get.id != 0L )
        assert( user.get.firstName == "john" )
        assert( user.get.lastName == "doe" )
        assert( user.get.createdAt.toString != "")
        assert( user.get.updatedAt.toString != "")
        //assert( user.get.uniqueId != "")
      }


      it("can update an item") {
        val svc = service()

        svc.create(new User(0).init("john", "doe"))
        var user = svc.get(1)
        val originalUniqueId = user.get.uniqueId
        val originalCreateDate = user.get.createdAt.toStringYYYYMMDDHHmmss()
        //val originalUpdateDate = user.get.updatedAt.toStringYYYYMMDDHHmmss()
        user.get.init("clark", "kent")
        svc.update(user.get)
        user = svc.get(1)

        assert( user.isDefined )
        assert( user.get.firstName == "clark" )
        assert( user.get.lastName == "kent" )
        assert( user.get.createdAt.toStringYYYYMMDDHHmmss == originalCreateDate)
        //assert( user.get.updatedAt.toStringYYYYMMDDHHmmss != originalUpdateDate)
        assert( user.get.uniqueId == originalUniqueId)

      }


      it("can get all items") {
        val svc = service()

        svc.create(new User(0).init("clark", "kent"))
        svc.create(new User(0).init("bruce", "wayne"))
        val users = svc.getAll()

        assert( users(0).id != 0L )
        assert( users(1).id != 0L )
        assert( users.length == 2 )
      }


      it("can delete an item") {

        val svc = service()

        svc.create(new User(0).init("clark", "kent"))
        svc.create(new User(0).init("bruce", "wayne"))
        val user = svc.get(2)
        svc.delete(user)
        val users = svc.getAll()

        assert( users.length == 1 )
      }
    }
  }


  private def service(): EntityService[User] = {
    // 1. Setup the mapper
    //val model = Mapper.loadSchema(typeOf[User])
    //val mapper = new EntityMapper(model)

    // 2. Setup repo
    val repo = new EntityRepoInMemory[User](typeOf[User])

    // 3. Setup service
    val svc = new EntityService[User](repo)
    svc
  }
}

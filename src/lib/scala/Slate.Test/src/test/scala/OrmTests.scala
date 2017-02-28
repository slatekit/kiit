import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.common.databases.{DbTypeMySql, DbLookup}
import slate.core.common.Conf
import slate.entities.core.{EntityService, Entities}
import slate.ext.invites.{InviteService, Invite}
import scala.reflect.runtime.universe.{typeOf, Type}


/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */


class OrmTests extends FunSpec with BeforeAndAfter with BeforeAndAfterAll {

  describe( "ORM" ) {


    describe("Case Classes") {

      it("can create an item") {
        val ent = setup("dbtests")
        val svc = ent.getService(typeOf[Invite]).asInstanceOf[InviteService]

        val item1:Invite = new Invite(email = "kishore@slatekit.com",
          password = "abc", userName = "kishore", promoCode = "123",
          firstName = "kishore", lastName = "reddy")

        // Test create
        val id = svc.create(item1)
        assert( id != 0 )

        // Test get
        val item2 = svc.get(id)
        assert( item2.isDefined )
        assert( item2.get.id == id )
        assert( item2.get.firstName == item1.firstName )

        // Test update
        val item3 = item2.get.copy(email = "min@slatekit.com", userName = "min", firstName = "amy")
        svc.update(item3)
      }
    }


    def setup(dbPrefix:String): Entities = {

      val conf = new Conf(Some("env.conf"))
      val db   = conf.dbCon(dbPrefix)
      val ent = new Entities(Some(new DbLookup(db)))

      // Register the invite service
      ent.register[Invite](isSqlRepo= true,
          entityType = typeOf[Invite],
          serviceType= Some(typeOf[InviteService]),
          dbType= Some(DbTypeMySql))

      ent
    }
  }
}

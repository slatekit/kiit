/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec}
import slate.common.{IocRunTime, DateTime}
import slate.common.query.{Query, QueryEncoder}
import scala.reflect.runtime.universe._

class IocTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }

  class EmailSvc1 { val provider = "sendgrid" }
  class TextSvc2  { val provider = "twilio"   }


  describe ( "Registration") {

    it("can register by key") {
      val ioc = new IocRunTime().register("email", new EmailSvc1())
      assert( ioc.contains("email") )
    }


    it("can register by type") {
      val svc = new EmailSvc1()
      val key = svc.getClass.getTypeName
      val ioc = new IocRunTime().register(svc)
      assert( ioc.contains(key) )
    }


    it("can get by key") {
      val ioc = new IocRunTime().register("email", new EmailSvc1())
      assert( ioc.contains("email") )
      assert( ioc.get[EmailSvc1]("email").isDefined )
      assert( ioc.get[EmailSvc1]("email").get.provider == "sendgrid")
    }


    it("can get by type") {
      val svc = new EmailSvc1()
      val key = svc.getClass.getTypeName
      val ioc = new IocRunTime().register(svc)
      assert( ioc.getAs[EmailSvc1]().isDefined )
      assert( ioc.getAs[EmailSvc1]().get.provider == "sendgrid")
    }
  }
}

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

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.common.http.common.HttpStatus
import slate.common.results.ResultCode
import slate.common._
import slate.common.http.{HttpRequest, HttpClient}
import slate.common.templates.{TemplatePart, Template, Templates}
import slate.core.sms.{SmsMessage, SmsServiceTwilio, SmsService}
import slate.common.http.HttpHelper._

class SmsTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }


  describe ( "registration") {

    it("can send without template") {
      val svc = new SmsServiceTwilio("", "", "", sender = Some((req) => simulatePost(req)))
      val io = svc.send(new SmsMessage("welcome", "us", "123-456-7890"))
      val result = io.run()
      println(result)
    }


    it("can send with template") {
      val svc = new SmsServiceTwilio("", "", "", sender = Some((req) => simulatePost(req)))
      val io = svc.send(new SmsMessage("welcome", "us", "123-456-7890"))
      val result = io.run()
      println(result)
    }
  }


  def simulatePost(req:HttpRequest): IO[Result[Boolean]] = {
    new IO[Result[Boolean]]( () => new SuccessResult[Boolean](true, 200, Some("Simulation : " + req.params.get(2)._2)))
  }


  def getTemplates():Templates = {
    Templates(
      Seq(
        new Template("welcome", "Hi @{user.name}, Welcome to @{company.name}."),
        new Template("confirm", "Your confirmation code for @{app.name} is @{code}.")
      ),
      Some(List(
        ("company.name" , (s:TemplatePart) => "CodeHelix"                       ),
        ("app.name"     , (s:TemplatePart) => "slatekit.sampleapp"              ),
        ("user.name"    , (s:TemplatePart) => "john.doe"                        ),
        ("code"         , (s:TemplatePart) => Random.alpha6()                   )
      ))
    )
  }
}

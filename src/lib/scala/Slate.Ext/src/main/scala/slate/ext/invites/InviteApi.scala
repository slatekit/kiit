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

package slate.ext.invites


import slate.common.{Result}
import slate.core.apis.{ApiAction, Api}
import slate.core.common.svcs.ApiEntityWithSupport
import slate.core.common.Conf
import slate.core.sms.{SmsServiceTwilio}
import scala.reflect.runtime.universe.typeOf


@Api(area = "app", name = "invites", desc = "supports operations on invitations",
  roles = "@admin", auth = "app", verb = "*", protocol = "*")
class InviteApi() extends ApiEntityWithSupport[Invite, InviteService] {

  @ApiAction(name = "", desc = "creates a new invitee", roles = "@parent" )
  def create(name:String, email:String, phone:String, promocode:String, country:String, platform:String): Result[Boolean] =
  {
    ok(Some("created invite"))
  }


  @ApiAction(name = "", desc = "sends an sms", roles = "@parent" )
  def sendSms(phone:String, promocode:String): Result[Boolean] =
  {
    this.context.cfg.apiKey("sms").fold[Result[Boolean]]( failure() )( apiKey =>
    {
      val sms = new SmsServiceTwilio(apiKey.key, apiKey.pass, apiKey.account)
      val result = sms.send("slate-" + promocode, "us", phone)
      result
    })
  }


  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[Invite]).asInstanceOf[InviteService]
    initContext(svc)
  }
}

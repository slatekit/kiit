/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */
package slate.ext.reg

import slate.common._
import slate.common.encrypt.Encryptor
import slate.common.results.{ResultSupportIn}
import slate.core.apis.{ApiAction, ApiBaseEntity, Api}
import slate.core.email.EmailService
import slate.core.mobile.MessageService
import slate.core.sms.SmsService
import slate.ext.devices.{Device, DeviceService}
import slate.ext.users.{UserService, User}
import scala.reflect.runtime.universe.typeOf

@Api(area = "app", name = "reg", desc = "api to manage users and registrations",
  roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class RegApi() extends ApiBaseEntity[User] with ResultSupportIn {


  @ApiAction(name = "", desc="activates the user", roles= "@parent")
  def activate(id:String): Result[Boolean] =
  {
    //success("regapi.activate")
    service.activate(id)
  }


  @ApiAction(name = "", desc="deactivates the user", roles= "@parent")
  def deactivate(id:String):Result[Boolean] =
  {
    //success("regapi.deactivate")
    service.deactivate(id)
  }


  @ApiAction(name = "", desc="confirms the users device via matching code", roles= "@parent")
  def confirmDevice(id:String, code:Int): Result[Boolean] =
  {
    //success("regapi.confirmDevice")
    service.confirmDevice(id, code)
  }


  @ApiAction(name = "", desc="confirms the users phone via matching code", roles= "@parent")
  def confirmPhone(id:String, code:Int): Result[Boolean] =
  {
    //success("regapi.confirmPhone")
    service.confirmPhone(id, code)
  }


  @ApiAction(name = "", desc="confirms the users email via matching code", roles= "@parent")
  def confirmEmail(id:String, code:Int): Result[Boolean] =
  {
    //success("regapi.confirmEmail")
    service.confirmEmail(id, code)
  }


  @ApiAction(name = "", desc="registers the user", roles= "@parent")
  def register(info:RegInfo): Result[Boolean] =
  {
    //success("regapi.register")
    service.register(info)
  }


  @ApiAction(name = "", desc="update device id", roles= "@parent")
  def updateDeviceId(userId:String, deviceId:String):Result[Boolean] = {
    service.updateDeviceId(userId, deviceId)
  }


  @ApiAction(name = "", desc="resets the password", roles= "@parent")
  def resetPass(id:String): Result[Boolean] =
  {
    notImplemented(Some("regapi.resetPassword"))
  }


  @ApiAction(name = "", desc="sends a confirmation sms", roles= "@parent")
  def sendConfirmSms(id:String): Result[Boolean] =
  {
    //success("regapi.activate")
    service.sendConfirmSmsById(id)
  }


  @ApiAction(name = "", desc="sends a confirmation sms", roles= "@parent")
  def sendConfirmDeviceById(id:String):Result[Boolean] = {
    service.sendConfirmDeviceById(id)
  }


  @ApiAction(name = "", desc="sends a confirmation email", roles= "@parent")
  def sendConfirmEmail(id:String): Result[Boolean] =
  {
    //success("regapi.sendConfirmEmail")
    service.sendConfirmEmailById(id)
  }


  @ApiAction(name = "", desc="signs the user in", roles= "@parent")
  def signIn(id:String, password:String): Result[String] =
  {
    //success("regapi.signIn")
    service.signIn(id, password)
  }


  @ApiAction(name = "", desc="signs the user out", roles= "@parent")
  def signOut(id:String): Result[String] =
  {
    //success("regapi.signOut")
    service.signOut(id)
  }


  @ApiAction(name = "", desc="creates a sample user", roles= "@parent")
  def createSampleUser(first:String, last:String, email:String, phone:String,
                       country:String, region:String): Result[Long] =
  {
    service.createSampleUser(first, last, email, phone, country, region)
  }


  override def init():Unit =
  {
    _service = context.ent.getService(typeOf[User]).asInstanceOf[UserService]
  }


  def service: RegService =
  {
    val users = context.ent.getService(typeOf[User]).asInstanceOf[UserService]
    val devices = context.ent.getService(typeOf[Device]).asInstanceOf[DeviceService]

    val sms = getSvc[SmsService]("sms")
    val email = getSvc[EmailService]("email")
    val msg = getSvc[MessageService]("msg")
    val reg = new RegService(this.context.enc.get, users, devices, sms, email, msg)
    val hooks = new RegHooks(devices, reg)
    reg.setHooks(hooks)
    reg
  }
}

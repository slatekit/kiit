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
package slate.ext.mobile

import slate.common.encrypt.Encryptor
import slate.common.{IocRunTime, DateTime}
import slate.core.apis.{ApiBase, Api, ApiAction}
import slate.core.mobile.{Message}
import scala.reflect.runtime.universe.typeOf


@Api(area = "app", name = "messages", desc= "access to audit history",
  roles= "@admin", auth = "app", verb = "*", protocol = "*")
class MessageApi(val _encryptor:Encryptor) extends ApiBase{


  @ApiAction(name = "", desc= "sets up the default resources", roles= "@parent")
  def send(userId:String, destId:String, msg:Message):Unit = {
    service.send(userId, destId, msg)
  }


  @ApiAction(name = "", desc= "sends a message with title and details supplied", roles= "@parent")
  def share(userId:String, destId:String,  title:String, details:String, data:String, date:DateTime, msgType:String, msgAction:String, msgTag:String):Unit = {
    service.share(userId, destId, title, details, data, date, msgType, msgAction, msgTag)
  }


  @ApiAction(name = "", desc= "sends a message with title and details supplied", roles= "@parent")
  def alert(userId:String, destId:String, title:String, details:String, data:String, date:DateTime):Unit = {
    service.alert(userId, destId, title, details, data, date)
  }


  @ApiAction(name = "", desc= "sends a message with title and details supplied", roles= "@parent")
  def msg(userId:String, destId:String, title:String, details:String, data:String, date:DateTime):Unit = {
    service.msg(userId, destId, title, details, data, date)
  }

  override def init():Unit =
  {
  }


  def service: MessageUserService =
  {
    // Using this requires the service to be registered
    getSvc[MessageUserService]("msg").get
  }

}

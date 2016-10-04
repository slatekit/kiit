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

package slate.ext.users

import slate.core.apis.{ApiAction, Api}
import slate.core.common.svcs.ApiEntityWithSupport
import scala.reflect.runtime.universe.typeOf

/**
  * Created by kreddy on 3/29/2016.
  */

@Api(area = "app", name = "users", desc= "access to users data",
  roles= "ops", auth = "key-roles", verb = "*", protocol = "*")
class UserApi  extends ApiEntityWithSupport[User, UserService] {


  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[User]).asInstanceOf[UserService]
    initContext(svc)
  }


  @ApiAction(name = "", desc="gets the confirm code by user id", roles= "@parent")
  def getPhoneConfirmCode(id:Long): Long = {
    _service.asInstanceOf[UserService].getPhoneConfirmCode(id)
  }

  @ApiAction(name = "", desc="gets the user by email", roles= "@parent")
  def getUserByEmail(email:String): Option[User] = {
    _service.asInstanceOf[UserService].getUserByEmail(email)
  }
}

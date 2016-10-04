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
package sampleapp.core.services

import sampleapp.core.models.User
import slate.common.results.ResultSupportIn
import slate.common.{Result, DateTime}
import slate.core.apis.{ApiArg, ApiAction, Api}
import slate.core.common.svcs.ApiEntityWithSupport
import scala.reflect.runtime.universe.{typeOf}


@Api(area = "sampleapp", name = "users", desc = "api for users", roles= "ops", auth = "app-roles", verb = "post", protocol = "*")
class UserApi extends ApiEntityWithSupport[User, UserService] with ResultSupportIn {

  @ApiAction(name= "", desc= "creates a new user", roles= "@parent", verb = "@parent", protocol = "*")
  def create(email:String, first:String, last:String, isMale:Boolean, age:Int, phone:String, country:String): User =
  {
    service.create(email, first, last, isMale, age, phone, country)
  }


  @ApiAction(name = "", desc = "activates a users account 3", roles= "admin", verb = "@parent", protocol = "@parent")
  def activate(id:Int, phone:String, code:Int, isPremiumUser:Boolean, date:DateTime): Result[String] =
  {
    service.activate(id, phone, code, isPremiumUser, date)
  }


  @ApiAction(name = "", desc = "gets the current promo code", roles= "@parent", verb = "post", protocol = "@parent")
  def promoCode(year:Int, month:Int, region:String): Result[String] =
  {
    service.promoCode(year, month, region)
  }


  @ApiAction(name = "", desc = "gets the total users for the region", roles= "@parent", verb = "*", protocol = "@parent")
  def getTotal(region:String): Int =
  {
    service.getTotal(region)
  }


  @ApiAction(name = "", desc = "gets the total users for the region", roles= "@parent", verb = "@parent", protocol = "@parent")
  def delete(region:String): Int =
  {
    service.getTotal(region)
  }


  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[User]).asInstanceOf[UserService]
    initContext(svc)
  }
}

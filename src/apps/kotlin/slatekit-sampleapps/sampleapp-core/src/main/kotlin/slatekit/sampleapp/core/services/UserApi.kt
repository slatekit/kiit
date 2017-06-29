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
package slatekit.sampleapp.core.services

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.svcs.ApiEntityWithSupport
import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.core.common.AppContext
import slatekit.sampleapp.core.models.User


@Api(area = "sampleapp", name = "users", desc = "api for users", roles= "ops", auth = "app-roles", verb = "post", protocol = "*")
class UserApi( context: AppContext)
  : ApiEntityWithSupport<User, UserService>(context, User::class) {

  @ApiAction(name= "", desc= "creates a new user", roles= "@parent", verb = "post", protocol = "*")
  fun create(email:String, first:String, last:String, isMale:Boolean, age:Int, phone:String, country:String): User
  {
    return service().create(email, first, last, isMale, age, phone, country)
  }


  @ApiAction(name= "", desc= "updates", roles= "@parent", verb = "put", protocol = "*")
  fun updatePhone(id:Long, phone:String): Result<String>
  {
    return service().updatePhone(id,phone)
  }


  @ApiAction(name = "", desc = "gets the total users for the region", roles= "@parent", verb = "delete", protocol = "@parent")
  fun deleteById(id:Long): Boolean
  {
    return service().delete(id)
  }


  @ApiAction(name = "", desc = "activates a users account 3", roles= "admin", verb = "@parent", protocol = "@parent")
  fun activate(id:Long, phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Result<String>
  {
    return service().activate(id, phone, code, isPremiumUser, date)
  }


  @ApiAction(name = "", desc = "gets the current promo code", roles= "@parent", verb = "post", protocol = "@parent")
  fun promoCode(year:Int, month:Int, region:String): Result<String>
  {
    return service().promoCode(year, month, region)
  }


  @ApiAction(name = "", desc = "gets the total users for the region", roles= "@parent", verb = "*", protocol = "@parent")
  fun getTotal(region:String): Int
  {
    return service().getTotal(region)
  }
}

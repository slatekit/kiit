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

package slatekit.sampleapp.core.services


import slatekit.common.DateTime
import slatekit.common.Random
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.core.common.AppContext
import slatekit.entities.support.EntityServiceWithSupport
import slatekit.entities.core.EntityRepo
import slatekit.integration.common.AppEntContext
import slatekit.sampleapp.core.models.User

/**
 * The EntityService
 */
class UserService(context: AppEntContext, repo:EntityRepo<User>)
                  : EntityServiceWithSupport<User>(context,repo)
{
  fun create(email:String, first:String, last:String, isMale:Boolean, age:Int, phone:String, country:String): User
  {
    val user = User(
      email = email,
      firstName = first,
      lastName = last,
      token = Random.stringGuid(false),
      userName = email,
      password = "12356789",
      country = country,
      phone = phone,
      roles = "user",
      status = "pending"
    )
    val id:Long = create(user)
    return user.copy(id = id)
  }


  fun updatePhone(id:Long, phone:String): Result<String>
  {
    val user = get(id)
    return user?.let { u ->
      val updated = u.copy(phone = phone)
      update(updated)
      success("updated phone : " + updated.phone)
    } ?: failure("unable to find user with id : " + id)
  }


  fun activate(id:Long, phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Result<String>
  {
    val user = get(id)
    return user?.let { u ->
      val updated = u.copy(status = "active")
      update(updated)
      success("activated user : " + u.email)
    } ?: failure("unable to find user with id : " + id)
  }


  fun promoCode(year:Int, month:Int, region:String): Result<String>
  {
    return success("promo code: ${year}-${month}-${region}")
  }


  fun getTotal(region:String): Int {
    return 30
  }
}

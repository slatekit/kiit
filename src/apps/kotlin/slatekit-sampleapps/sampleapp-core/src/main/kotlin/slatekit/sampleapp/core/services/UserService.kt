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
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityRepo
import slatekit.entities.services.EntityServiceWithSupport
import slatekit.integration.common.AppEntContext
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.sampleapp.core.models.User

/**
 * The EntityService
 */
class UserService(context: AppEntContext, entities: Entities, repo:EntityRepo<Long, User>)
                  : EntityServiceWithSupport<Long, User>(context, entities, repo)
{
  fun create(email:String, first:String, last:String, isMale:Boolean, age:Int, phone:String, country:String): User
  {
    val user = User(
      email = email,
      firstName = first,
      lastName = last,
      token = Random.uuid(),
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


  fun updatePhone(id:Long, phone:String): Notice<String>
  {
    val user = get(id)
    return user?.let { u ->
      val updated = u.copy(phone = phone)
      update(updated)
      Success("updated phone : " + updated.phone)
    } ?: Failure("unable to find user with id : " + id)
  }


  fun activate(id:Long, phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Notice<String>
  {
    val user = get(id)
    return user?.let { u ->
      val updated = u.copy(status = "active")
      update(updated)
      Success("activated user : " + u.email)
    } ?: Failure("unable to find user with id : " + id)
  }


  fun promoCode(year:Int, month:Int, region:String): Notice<String>
  {
    return Success("promo code: ${year}-${month}-${region}")
  }


  fun getTotal(region:String): Int {
    return 30
  }
}

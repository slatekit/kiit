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

package sampleapp.core.services

import sampleapp.core.models.User
import slate.common.{Random, Result, DateTime}
import slate.core.common.svcs.EntityServiceWithSupport

/**
 * The EntityService
 */
class UserService extends EntityServiceWithSupport[User]()
{

  def create(email:String, first:String, last:String, isMale:Boolean, age:Int, phone:String, country:String): User =
  {
    val user = new User()
    user.email = email
    user.firstName = first
    user.lastName = last
    user.token = Random.stringGuid(false)
    user.userName = email
    user.password = "12356789"
    user.country = country
    user.phone = phone
    user.roles = "user"
    user.status = "pending"
    create(user)
    user
  }


  def updatePhone(id:Int, phone:String): Result[String] =
  {
    val user = get(id)
    if(!user.isDefined){
      return failure(Some("unable to find user with id : " + id))
    }
    user.get.phone = phone
    update(user.get)
    success(s"updated phone : " + user.get.phone)
  }


  def activate(id:Int, phone:String, code:Int, isPremiumUser:Boolean, date:DateTime): Result[String] =
  {
    val user = get(id)
    if(!user.isDefined){
      return failure(Some("unable to find user with id : " + id))
    }
    user.get.status = "active"
    update(user.get)
    success(s"activated user : " + user.get.email)
  }


  def promoCode(year:Int, month:Int, region:String): Result[String] =
  {
    success(s"promo code: ${year}-${month}-${region}")
  }


  def getTotal(region:String): Int =
  {
    30
  }
}

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

package slate.test.common

import slate.common.results.ResultSupportIn
import slate.common.{DateTime, Result}
import slate.core.apis._

@Api(area = "app", name = "users", desc = "api to access and manage users 3",
  roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class UserApi extends ApiBaseEntity[User] with ResultSupportIn
{
  var user = new User

  @ApiAction(name = "", desc = "activates a users account 3", roles= "", verb = "get", protocol = "@parent")
  def info(format:String = "json"): Result[String] =
  {
    success("ok", Some(s"info"))
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "cli")
  def protocolCLI(code:Int, tag:String): Result[String] =
  {
    success("protocolCLI", Some(s"${code} ${tag}"))
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "web")
  def protocolWeb(code:Int, tag:String): Result[String] =
  {
    success("protocolWeb", Some(s"${code} ${tag}"))
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "*")
  def protocolAny(code:Int, tag:String): Result[String] =
  {
    success("protocolAny", Some(s"${code} ${tag}"))
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "@parent")
  def protocolParent(code:Int, tag:String): Result[String] =
  {
    success("protocolParent", Some(s"${code} ${tag}"))
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "@parent")
  def rolesNone(code:Int, tag:String): Result[String] =
  {
    success("rolesNone", Some(s"${code} ${tag}"))
  }


  @ApiAction(name = "", desc = "", roles= "*", verb = "@parent", protocol = "@parent")
  def rolesAny(code:Int, tag:String): Result[String] =
  {
    success("rolesAny", Some(s"${code} ${tag}"))
  }


  @ApiAction(name = "", desc = "", roles= "dev", verb = "@parent", protocol = "@parent")
  def rolesSpecific(code:Int, tag:String): Result[String] =
  {
    success("rolesSpecific", Some(s"${code} ${tag}"))
  }


  @ApiAction(name = "", desc = "", roles= "@parent", verb = "@parent", protocol = "@parent")
  def rolesParent(code:Int, tag:String): Result[String] =
  {
    success("rolesParent", Some(s"${code} ${tag}"))
  }


  @ApiAction(name = "activate", desc = "activates a users account 3", roles= "@parent",
    verb = "@parent", protocol = "@parent")
  def activate(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime): Result[String] =
  {
    success("ok", Some(s"activated $phone, $code, $isPremiumUser, $date"))
  }


  @ApiAction(name = "", desc = "invites a new user", roles= "@parent", verb = "@parent", protocol = "@parent")
  def invite(email:String, phone:String, promoCode:String): Result[String] =
  {
    success("ok", Some(s"sent invitation to $email, $phone, $promoCode"))
  }



  @ApiAction(name = "", desc = "invites a new user", roles= "@parent", verb = "@parent", protocol = "@parent")
  def register(user:User): Result[String] =
  {
    success("ok", Some(s"object user"))
  }


  def create(email:String, first:String, last:String, isMale:Boolean, age:Int): User =
  {
    user.email = email
    user.firstName = first
    user.lastName = last
    user.isMale = isMale
    user.age = age
    user
  }
}

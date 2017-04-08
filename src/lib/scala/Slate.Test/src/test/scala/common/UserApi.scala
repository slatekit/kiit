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

import scala.reflect.runtime.universe.{typeOf}
import slate.common.encrypt.{DecString, DecLong, DecDouble, DecInt}
import slate.common.results.{ResultCode, ResultSupportIn}
import slate.common.{Doc, FailureResult, DateTime, Result}
import slate.core.apis._
import slate.core.common.AppContext

@Api(area = "app", name = "users2", desc = "api to access and manage users 3",
  roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class UserApi2(context:AppContext) extends ApiBaseEntity[User](context, typeOf[User]) with ResultSupportIn {

  override val isErrorEnabled = true


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "@parent")
  def testException(code:Int, tag:String): Result[String] =
  {
    throw new Exception("Test unhandled exception")
  }
}


@Api(area = "app", name = "users2", desc = "api to access and manage users 3",
  roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class UserApi3(context:AppContext) extends ApiBaseEntity[User](context, typeOf[User]) with ResultSupportIn {

  override val isErrorEnabled = true

  /**
   * Handle error at the API level
    *
    * @param context
   * @param request
   * @param ex
   * @return
   */
  override def onException(context:AppContext, request: Request, ex:Exception): Result[Any] = {
    unexpectedError(msg = Some("unexpected error in api"), err = Some(ex))
  }
}


@Api(area = "app", name = "users", desc = "api to access and manage users 3",
  roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class UserApi(context:AppContext) extends ApiBaseEntity[User](context, typeOf[User]) with ResultSupportIn
{
  var user = new User

  @ApiAction(name = "activate", desc = "activates a users account 3", roles= "@parent",
    verb = "@parent", protocol = "@parent")
  def activate(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime): Result[String] =
  {
    success("ok", Some(s"activated $phone, $code, $isPremiumUser, $date"))
  }


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
  def testException(code:Int, tag:String): Result[String] =
  {
    throw new Exception("Test unhandled exception")
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


  @ApiAction(name = "", desc = "test decryption of int", roles= "*", verb = "@parent", protocol = "@parent")
  def decInt(id:DecInt): Result[String] =
  {
    success("ok", Some(s"decrypted int : " + id.value))
  }


  @ApiAction(name = "", desc = "test decryption of int", roles= "*", verb = "@parent", protocol = "@parent")
  def decLong(id:DecLong): Result[String] =
  {
    success("ok", Some(s"decrypted long : " + id.value))
  }


  @ApiAction(name = "", desc = "test decryption of int", roles= "*", verb = "@parent", protocol = "@parent")
  def decDouble(id:DecDouble): Result[String] =
  {
    success("ok", Some(s"decrypted double : " + id.value))
  }


  @ApiAction(name = "", desc = "test decryption of int", roles= "*", verb = "@parent", protocol = "@parent")
  def decString(id:DecString): Result[String] =
  {
    success("ok", Some(s"decrypted string : " + id.value))
  }


  @ApiAction(name = "testArgs", desc = "test types", roles= "*", verb = "@parent", protocol = "@parent")
  def testArgs(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime, key:DecString): Result[String] =
  {
    success("ok", Some(s"$phone $code $isPremiumUser, $key"))
  }


  @ApiAction(name = "", desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  def argTypeRequest(req:Request): Result[String] =
  {
    success("ok", Some("raw request id: " + req.args.get.getInt("id")))
  }


  @ApiAction(name = "", desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  def argTypeFile(doc:Doc): Result[String] =
  {
    success("ok", Some(doc.content))
  }


  @ApiAction(name = "", desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  def argTypeListString(items:List[String]): Result[String] =
  {
    success("ok", Some(items.fold("")( (acc, curr) => acc + "," + curr)))
  }


  @ApiAction(name = "", desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  def argTypeListInt(items:List[Int]): Result[String] =
  {
    success("ok", Some(items.foldLeft("")( (acc, curr) => acc + "," + curr.toString)))
  }


  @ApiAction(name = "", desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  def argTypeMapInt(items:Map[String,Int]): Result[String] =
  {
    val sortedPairs = items.keys.toList.sortBy( k => k ).map( key => (key, items(key)))
    val delimited = sortedPairs.foldLeft("")( (acc, curr) => acc + "," + curr._1 + "=" + curr._2 )
    success("ok", Some(delimited))
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

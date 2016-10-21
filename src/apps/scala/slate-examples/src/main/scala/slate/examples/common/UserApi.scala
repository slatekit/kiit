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

package slate.examples.common

import slate.common.results.ResultSupportIn
import slate.common.{DateTime, Result}
import slate.core.apis._

@Api(area = "app", name = "users", desc = "api to access and manage users 3",
  roles= "@admin", auth = "app", protocol = "*")
class UserApi extends ApiBaseEntity[User] with ResultSupportIn
{
  var user = new User

  @ApiAction(name = "", desc = "activates a users account 3", roles= "@parent")
  @ApiArg("phone", "the phone number", true, "1-234-567-8901")
  def info(format:String = "json"): Result[Boolean] =
  {
    ok(Some(s"info"))
  }


  @ApiAction(name = "", desc = "activates a users account 3", roles= "@parent")
  @ApiArg("phone", "the phone number", true, "1-234-567-8901")
  def activate(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime): Result[Boolean] =
  {
    ok(Some(s"activated $phone, $code, $isPremiumUser, $date"))
  }


  @ApiAction(name = "", desc = "invites a new user", roles= "@parent")
  //@ApiArg(name = "email"    , desc = "the email of invitee", required = true, eg = "johndoe@gmail.com")
  //@ApiArg(name = "phone"    , desc = "the phone of invitee", required = true, eg = "12345678901")
  //@ApiArg(name = "promoCode", desc = "promotion code"      , required = true, eg = "abc")
  def invite(email:String, phone:String, promoCode:String): Result[Boolean] =
  {
    ok(Some(s"sent invitation to $email, $phone, $promoCode"))
  }



  @ApiAction(name = "", desc = "invites a new user", roles= "@parent")
  //@ApiArg(name = "email"    , desc = "the email of invitee", required = true, eg = "johndoe@gmail.com")
  //@ApiArg(name = "phone"    , desc = "the phone of invitee", required = true, eg = "12345678901")
  //@ApiArg(name = "promoCode", desc = "promotion code"      , required = true, eg = "abc")
  def register(user:User): Result[Boolean] =
  {
    ok(Some(s"object user"))
  }



  //@ApiAction(desc= "creates a new user", roles= "@parent", args= "[" +
  //  "{ name = 'email' , desc = 'the email of invitee', required = true, eg = 'johndoe@gmail.com'}," +
  //  "{ name = 'first' , desc = 'the first name'      , required = true, eg = 'john'             }," +
  //  "{ name = 'last'  , desc = 'the last name'       , required = true, eg = 'doe'              }," +
  //  "{ name = 'isMale', desc = 'whether user is male', required = true, eg = 'true|false'       }," +
  //  "{ name = 'age'   , desc = 'age of the user'     , required = true, eg = '35'               })" +
  //  "]")
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

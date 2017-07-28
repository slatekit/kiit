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

package slatekit.examples.common

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.ApiArg
import slatekit.integration.common.ApiBaseEntity
import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.common.AppContext


@Api(area = "app", name = "users", desc = "api to access and manage users 3",
     roles= "admin", auth = "app", verb = "*", protocol = "*")
class UserApi(ctx:AppContext) : ApiBaseEntity<User>(ctx, User::class) {

  @ApiAction(name = "", desc = "activates a users account 3", roles= "@parent")
  fun activate(phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Result<Boolean> {
    return ok("activated $phone, $code, $isPremiumUser, $date")
  }

  var _user = User()


  /**
   * The api action is used to tell the slatekit system to make this method
   * available as an api action.
   * NOTE: The ApiArg annotation is OPTIONAL
   */
  @ApiAction(name = "", desc = "activates a users account 3", roles= "@parent")
  @ApiArg("phone", "the phone number", true, "1-234-567-8901")
  fun info(format:String = "json"): Result<Boolean> {
    return ok("info")
  }


  @ApiAction(name = "", desc = "invites a new user", roles= "@parent")
  @ApiArg(name = "email"    , desc = "the email of invitee", required = true, eg = "johndoe@gmail.com")
  @ApiArg(name = "phone"    , desc = "the phone of invitee", required = true, eg = "12345678901")
  @ApiArg(name = "promoCode", desc = "promotion code"      , required = true, eg = "abc")
  fun invite(email:String, phone:String, promoCode:String): Result<Boolean> {
    return ok("sent invitation to $email, $phone, $promoCode")
  }


  @ApiAction(name = "", desc = "invites a new user", roles= "@parent")
  @ApiArg(name = "email"    , desc = "the email of invitee", required = true, eg = "johndoe@gmail.com")
  @ApiArg(name = "phone"    , desc = "the phone of invitee", required = true, eg = "12345678901")
  @ApiArg(name = "promoCode", desc = "promotion code"      , required = true, eg = "abc")
  fun register(user:User): Result<Boolean> {
    return ok("object user")
  }


  @ApiAction(name = "", desc= "creates a new user", roles= "@parent" )
  @ApiArg(name = "email" , desc = "the email of invitee", required = true, eg = "johndoe@gmail.com" )
  @ApiArg(name = "first" , desc = "the first api"      , required = true, eg = "john"              )
  @ApiArg(name = "last"  , desc = "the last api"       , required = true, eg = "doe"               )
  @ApiArg(name = "isMale", desc = "whether user is male", required = true, eg = "true|false"        )
  @ApiArg(name = "age"   , desc = "age of the user"     , required = true, eg = "35"                )
  fun create(email:String, first:String, last:String, isMale:Boolean, age:Int): User {

    _user = _user.copy(email = email,
              firstName = first,
              lastName = last,
              isMale = isMale,
              age = age
    )
    return _user.copy()
  }
}

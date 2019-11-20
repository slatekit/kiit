/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.examples.common

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.Input
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.common.*
import slatekit.common.auth.Roles
import slatekit.integration.common.ApiBaseEntity
import slatekit.integration.common.AppEntContext
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try


@Api(area = "app", name = "users", desc = "api to access and manage users 3",
        auth = AuthModes.TOKEN, roles = [Roles.ALL], verb = Verbs.AUTO, sources = [Sources.ALL])
class UserApi(ctx: AppEntContext) : ApiBaseEntity<Long, User, UserService>(ctx, Long::class, User::class, UserService(ctx.ent, ctx.ent.getRepo(User::class))) {

  @Action(desc = "activates a users account 3")
  fun activate(phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Try<String> {
    return Success("activated $phone, $code, $isPremiumUser, $date")
  }

  var _user = User()


  /**
   * The api action is used to tell the slatekit system to make this method
   * available as an api action.
   * NOTE: The Input annotation is OPTIONAL
   */
  @Action(name = "", desc = "activates a users account 3")
  @Input("phone", "the phone number", true, "1-234-567-8901")
  fun info(format:String = "json"): Notice<Boolean> {
    return Success(true,"info")
  }


  @Action(name = "", desc = "invites a new user")
  @Input(name = "email"    , desc = "the email of invitee", required = true, examples = ["johndoe@gmail.com"])
  @Input(name = "phone"    , desc = "the phone of invitee", required = true, examples = [ "12345678901"])
  @Input(name = "promoCode", desc = "promotion code"      , required = true, examples = [ "abc"])
  fun invite(email:String, phone:String, promoCode:String): Notice<Boolean> {
    return Success(true,"sent invitation to $email, $phone, $promoCode")
  }


  @Action(name = "", desc = "invites a new user")
  @Input(name = "email"    , desc = "the email of invitee", required = true, examples = [ "johndoe@gmail.com"])
  @Input(name = "phone"    , desc = "the phone of invitee", required = true, examples = [ "12345678901"])
  @Input(name = "promoCode", desc = "promotion code"      , required = true, examples = [ "abc"])
  fun register(user:User): Notice<Boolean> {
    return Success(true, "object user")
  }


  @Action(name = "", desc= "creates a new user" )
  @Input(name = "email" , desc = "the email of invitee", required = true, examples =[ "johndoe@gmail.com"] )
  @Input(name = "first" , desc = "the first api"      , required = true,  examples =[ "john"              ])
  @Input(name = "last"  , desc = "the last api"       , required = true,  examples =[ "doe"               ])
  @Input(name = "isMale", desc = "whether user is male", required = true, examples =[ "true|false"       ] )
  @Input(name = "age"   , desc = "age of the user"     , required = true, examples =[ "35"               ] )
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

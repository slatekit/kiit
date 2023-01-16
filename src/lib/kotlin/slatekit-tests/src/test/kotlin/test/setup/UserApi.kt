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

package test.setup

import slatekit.apis.*
import kiit.common.*
import kiit.common.auth.Roles
import kiit.common.types.ContentFile
import kiit.common.crypto.EncDouble
import kiit.common.crypto.EncString
import kiit.common.crypto.EncInt
import kiit.common.crypto.EncLong
import kiit.common.values.Metadata
import kiit.requests.Request
import kiit.integration.common.ApiBaseEntity
import kiit.entities.EntityService
import slatekit.connectors.entities.AppEntContext
import kiit.results.Notice
import kiit.results.Success


@Api(area = "app", name = "users", desc = "api to access and manage users 3", roles= ["admin"], auth = AuthModes.TOKEN, verb = Verbs.AUTO, sources = [Sources.ALL])
class UserApi(context: AppEntContext)
  : ApiBaseEntity<Long, User, EntityService<Long, User>>(context, Long::class, User::class, context.ent.getService())
{

  @Action(name = "activate", desc = "activates a users account 3", roles= [Roles.PARENT], sources = [Sources.PARENT])
  @Input(name = "phone", desc = "phone number", examples = ["123-456-789"])
  @Input(name = "code", desc = "activation code", defaults = "0", examples = ["1234"])
  fun activate(phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Notice<String> =
    Success("ok", msg ="activated $phone, $code, $isPremiumUser, $date")


  @Action(desc = "activates a users account 3")
  @Input(name = "phone", desc = "phone number", examples = ["123-456-789"])
  @Input(name = "code", desc = "activation code", defaults = "0", examples = ["1234"])
  fun testTypes(phone:String, current:Boolean, code:Short, zip:Int, id:Long, rating:Float, value:Double, date: DateTime): Notice<String> =
          Success("ok", msg = "$phone, $current, $code, $zip, $id, $rating, $value, $date")


  @Action(name = "", desc = "activates a users account 3", roles= [""], verb = Verbs.GET)
  fun info(format:String = "json"): Notice<String> {
    return Success("ok", msg ="info")
  }

  
  @Action(sources = [Sources.CLI])
  fun protocolCLI(code:Int, tag:String): Notice<String> {
    return Success("protocolCLI", msg ="${code} ${tag}")
  }


  @Action(sources = [Sources.WEB])
  fun protocolWeb(code:Int, tag:String): Notice<String> {
    return Success("protocolWeb", msg ="${code} ${tag}")
  }


  @Action(sources = [Sources.ALL])
  fun protocolAny(code:Int, tag:String): Notice<String> {
    return Success("protocolAny", msg ="${code} ${tag}")
  }


  @Action()
  fun protocolParent(code:Int, tag:String): Notice<String> {
    return Success("protocolParent", msg ="${code} ${tag}")
  }


  @Action(desc = "")
  fun testException(code:Int, tag:String): Notice<String> {
    throw Exception("Test unhandled exception")
  }


  @Action(desc = "", roles= [Roles.NONE])
  fun rolesNone(code:Int, tag:String): Notice<String> {
    return Success("rolesNone", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles=[Roles.ALL])
  fun rolesAny(code:Int, tag:String): Notice<String> {
    return Success("rolesAny", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= ["dev"])
  fun rolesSpecific(code:Int, tag:String): Notice<String>  {
    return Success("rolesSpecific", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= [Roles.PARENT])
  fun rolesParent(code:Int, tag:String): Notice<String> {
    return Success("rolesParent", msg ="${code} ${tag}")
  }


  @Action(desc = "invites a new user", roles= [Roles.PARENT])
  fun invite(email:String, phone:String, promoCode:String): Notice<String> {
    return Success("ok", msg ="sent invitation to $email, $phone, $promoCode")
  }


  @Action(desc = "invites a new user", roles= [Roles.PARENT])
  fun register(user: User): Notice<String> {
    return Success("ok", msg ="object user")
  }


  @Action(desc = "test decryption of int", roles=[Roles.ALL])
  fun decInt(id: EncInt): Notice<String> {
    return Success("ok", msg ="decrypted int : " + id.value)
  }


  @Action(desc = "test decryption of long", roles=[Roles.ALL])
  fun decLong(id: EncLong): Notice<String> {
    return Success("ok", msg ="decrypted long : " + id.value)
  }


  @Action(desc = "test decryption of double", roles=[Roles.ALL])
  fun decDouble(id: EncDouble): Notice<String>
  {
    return Success("ok", msg ="decrypted double : " + id.value)
  }


  @Action(desc = "test decryption of string", roles=[Roles.ALL])
  fun decString(id: EncString): Notice<String>
  {
    return Success("ok", msg ="decrypted string : " + id.value)
  }


  @Action(name = "testArgs", desc = "test types", roles=[Roles.ALL])
  fun testArgs(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime, key: EncString): Notice<String>
  {
    return Success("ok", msg ="$phone $code $isPremiumUser, $key")
  }


  @Action(desc = "", roles=[Roles.ALL], verb = Verbs.POST)
  fun argTypeRequest(req: Request): Notice<String> {
    return Success("ok", msg ="raw send id: " + req.data!!.getInt("id"))
  }


  @Action(desc = "", roles=[Roles.ALL], verb = Verbs.POST)
  fun argTypeMeta(meta: Metadata): Notice<String> {
    return Success("ok", msg ="raw meta token: " + meta.get("token"))
  }


  @Action(desc = "gets the current promo code", roles=[Roles.ALL], verb = Verbs.POST)
  fun argTypeFile(doc: ContentFile): Notice<String> {
    return Success("ok", msg = String(doc.data))
  }


  @Action(desc = "gets the current promo code", roles=[Roles.ALL], verb = Verbs.POST)
  fun argTypeListString(items:List<String>): Notice<String> {
    return Success("ok", msg =items.fold("", { acc, curr -> acc + "," + curr } ))
  }


  @Action(desc = "gets the current promo code", roles=[Roles.ALL], verb = Verbs.POST)
  fun argTypeListInt(items:List<Int>): Notice<String> {
    return Success("ok", msg =items.fold("", { acc, curr -> acc + "," + curr.toString() } ))
  }


  @Action(desc = "gets the current promo code", roles=[Roles.ALL], verb = Verbs.POST)
  fun argTypeMapInt(items:Map<String,Int>): Notice<String> {
    val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
    val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
    return Success("ok", msg =delimited)
  }
}

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

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.setup.Input
import slatekit.common.*
import slatekit.common.auth.Roles
import slatekit.common.content.Doc
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncString
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.requests.Request
import slatekit.integration.common.ApiBaseEntity
import slatekit.entities.EntityService
import slatekit.integration.common.AppEntContext
import slatekit.results.Notice
import slatekit.results.Success


@Api(area = "app", name = "users", desc = "api to access and manage users 3", roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class UserApi(context: AppEntContext)
  : ApiBaseEntity<Long, User, EntityService<Long, User>>(context, Long::class, User::class, context.ent.getSvc(User::class))
{

  @Action(name = "activate", desc = "activates a users account 3", roles= "@parent", verb = "@parent", protocol = "@parent")
  @Input(name = "phone", desc = "phone number", eg = "123-456-789")
  @Input(name = "code", desc = "activation code", defaultVal = "0", eg = "1234")
  fun activate(phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Notice<String> =
    Success("ok", msg ="activated $phone, $code, $isPremiumUser, $date")


  @Action(desc = "activates a users account 3", roles= "@parent", verb = "@parent", protocol = "@parent")
  @Input(name = "phone", desc = "phone number", eg = "123-456-789")
  @Input(name = "code", desc = "activation code", defaultVal = "0", eg = "1234")
  fun testTypes(phone:String, current:Boolean, code:Short, zip:Int, id:Long, rating:Float, value:Double, date: DateTime): Notice<String> =
          Success("ok", msg = "$phone, $current, $code, $zip, $id, $rating, $value, $date")


  @Action(name = "", desc = "activates a users account 3", roles= "", verb = "get", protocol = "@parent")
  fun info(format:String = "json"): Notice<String> {
    return Success("ok", msg ="info")
  }

  
  @Action(protocol = "cli")
  fun protocolCLI(code:Int, tag:String): Notice<String> {
    return Success("protocolCLI", msg ="${code} ${tag}")
  }


  @Action(protocol = "web")
  fun protocolWeb(code:Int, tag:String): Notice<String> {
    return Success("protocolWeb", msg ="${code} ${tag}")
  }


  @Action(protocol = "*")
  fun protocolAny(code:Int, tag:String): Notice<String> {
    return Success("protocolAny", msg ="${code} ${tag}")
  }


  @Action()
  fun protocolParent(code:Int, tag:String): Notice<String> {
    return Success("protocolParent", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= "", verb = "@parent", protocol = "@parent")
  fun testException(code:Int, tag:String): Notice<String> {
    throw Exception("Test unhandled exception")
  }


  @Action(desc = "", roles= Roles.none, verb = "@parent", protocol = "@parent")
  fun rolesNone(code:Int, tag:String): Notice<String> {
    return Success("rolesNone", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= "*", verb = "@parent", protocol = "@parent")
  fun rolesAny(code:Int, tag:String): Notice<String> {
    return Success("rolesAny", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= "dev", verb = "@parent", protocol = "@parent")
  fun rolesSpecific(code:Int, tag:String): Notice<String>  {
    return Success("rolesSpecific", msg ="${code} ${tag}")
  }


  @Action(desc = "", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun rolesParent(code:Int, tag:String): Notice<String> {
    return Success("rolesParent", msg ="${code} ${tag}")
  }


  @Action(desc = "invites a new user", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun invite(email:String, phone:String, promoCode:String): Notice<String> {
    return Success("ok", msg ="sent invitation to $email, $phone, $promoCode")
  }


  @Action(desc = "invites a new user", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun register(user: User): Notice<String> {
    return Success("ok", msg ="object user")
  }


  @Action(desc = "test decryption of int", roles= "*", verb = "@parent", protocol = "@parent")
  fun decInt(id: EncInt): Notice<String> {
    return Success("ok", msg ="decrypted int : " + id.value)
  }


  @Action(desc = "test decryption of long", roles= "*", verb = "@parent", protocol = "@parent")
  fun decLong(id: EncLong): Notice<String> {
    return Success("ok", msg ="decrypted long : " + id.value)
  }


  @Action(desc = "test decryption of double", roles= "*", verb = "@parent", protocol = "@parent")
  fun decDouble(id: EncDouble): Notice<String>
  {
    return Success("ok", msg ="decrypted double : " + id.value)
  }


  @Action(desc = "test decryption of string", roles= "*", verb = "@parent", protocol = "@parent")
  fun decString(id: EncString): Notice<String>
  {
    return Success("ok", msg ="decrypted string : " + id.value)
  }


  @Action(name = "testArgs", desc = "test types", roles= "*", verb = "@parent", protocol = "@parent")
  fun testArgs(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime, key: EncString): Notice<String>
  {
    return Success("ok", msg ="$phone $code $isPremiumUser, $key")
  }


  @Action(desc = "", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeRequest(req: Request): Notice<String> {
    return Success("ok", msg ="raw send id: " + req.data!!.getInt("id"))
  }


  @Action(desc = "", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeMeta(meta: Metadata): Notice<String> {
    return Success("ok", msg ="raw meta token: " + meta.get("token"))
  }


  @Action(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeFile(doc: Doc): Notice<String> {
    return Success("ok", msg =doc.content)
  }


  @Action(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeListString(items:List<String>): Notice<String> {
    return Success("ok", msg =items.fold("", { acc, curr -> acc + "," + curr } ))
  }


  @Action(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeListInt(items:List<Int>): Notice<String> {
    return Success("ok", msg =items.fold("", { acc, curr -> acc + "," + curr.toString() } ))
  }


  @Action(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeMapInt(items:Map<String,Int>): Notice<String> {
    val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
    val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
    return Success("ok", msg =delimited)
  }
}

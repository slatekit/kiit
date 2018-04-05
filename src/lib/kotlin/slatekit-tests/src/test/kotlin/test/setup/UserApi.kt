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

package test.setup

import slatekit.common.results.ResultFuncs.success
import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.ApiArg
import slatekit.common.*
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncString
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.integration.common.ApiBaseEntity
import slatekit.entities.core.EntityService
import slatekit.integration.common.AppEntContext


@Api(area = "app", name = "users", desc = "api to access and manage users 3", roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class UserApi(context: AppEntContext): ApiBaseEntity<User, EntityService<User>>(context, User::class)
{

  @ApiAction(name = "activate", desc = "activates a users account 3", roles= "@parent", verb = "@parent", protocol = "@parent")
  @ApiArg(name = "phone", desc = "phone number", eg = "123-456-789")
  @ApiArg(name = "code", desc = "activation code", defaultVal = "0", eg = "1234")
  fun activate(phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Result<String> =
    success("ok", "activated $phone, $code, $isPremiumUser, $date")


  @ApiAction(desc = "activates a users account 3", roles= "@parent", verb = "@parent", protocol = "@parent")
  @ApiArg(name = "phone", desc = "phone number", eg = "123-456-789")
  @ApiArg(name = "code", desc = "activation code", defaultVal = "0", eg = "1234")
  fun testTypes(phone:String, current:Boolean, code:Short, zip:Int, id:Long, rating:Float, value:Double, date: DateTime): Result<String> =
          success("ok", "$phone, $current, $code, $zip, $id, $rating, $value, $date")


  @ApiAction(name = "", desc = "activates a users account 3", roles= "", verb = "get", protocol = "@parent")
  fun info(format:String = "json"): Result<String> {
    return success("ok", "info")
  }

  
  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "cli")
  fun protocolCLI(code:Int, tag:String): Result<String> {
    return success("protocolCLI", "${code} ${tag}")
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "web")
  fun protocolWeb(code:Int, tag:String): Result<String> {
    return success("protocolWeb", "${code} ${tag}")
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "*")
  fun protocolAny(code:Int, tag:String): Result<String> {
    return success("protocolAny", "${code} ${tag}")
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "@parent")
  fun protocolParent(code:Int, tag:String): Result<String> {
    return success("protocolParent", "${code} ${tag}")
  }


  @ApiAction(desc = "", roles= "", verb = "@parent", protocol = "@parent")
  fun testException(code:Int, tag:String): Result<String> {
    throw Exception("Test unhandled exception")
  }


  @ApiAction(desc = "", roles= "", verb = "@parent", protocol = "@parent")
  fun rolesNone(code:Int, tag:String): Result<String> {
    return success("rolesNone", "${code} ${tag}")
  }


  @ApiAction(desc = "", roles= "*", verb = "@parent", protocol = "@parent")
  fun rolesAny(code:Int, tag:String): Result<String> {
    return success("rolesAny", "${code} ${tag}")
  }


  @ApiAction(desc = "", roles= "dev", verb = "@parent", protocol = "@parent")
  fun rolesSpecific(code:Int, tag:String): Result<String>  {
    return success("rolesSpecific", "${code} ${tag}")
  }


  @ApiAction(desc = "", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun rolesParent(code:Int, tag:String): Result<String> {
    return success("rolesParent", "${code} ${tag}")
  }


  @ApiAction(desc = "invites a new user", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun invite(email:String, phone:String, promoCode:String): Result<String> {
    return success("ok", "sent invitation to $email, $phone, $promoCode")
  }


  @ApiAction(desc = "invites a new user", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun register(user: User): Result<String> {
    return success("ok", "object user")
  }


  @ApiAction(desc = "test decryption of int", roles= "*", verb = "@parent", protocol = "@parent")
  fun decInt(id: EncInt): Result<String> {
    return success("ok", "decrypted int : " + id.value)
  }


  @ApiAction(desc = "test decryption of long", roles= "*", verb = "@parent", protocol = "@parent")
  fun decLong(id: EncLong): Result<String> {
    return success("ok", "decrypted long : " + id.value)
  }


  @ApiAction(desc = "test decryption of double", roles= "*", verb = "@parent", protocol = "@parent")
  fun decDouble(id: EncDouble): Result<String>
  {
    return success("ok", "decrypted double : " + id.value)
  }


  @ApiAction(desc = "test decryption of string", roles= "*", verb = "@parent", protocol = "@parent")
  fun decString(id: EncString): Result<String>
  {
    return success("ok", "decrypted string : " + id.value)
  }


  @ApiAction(name = "testArgs", desc = "test types", roles= "*", verb = "@parent", protocol = "@parent")
  fun testArgs(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime, key: EncString): Result<String>
  {
    return success("ok", "$phone $code $isPremiumUser, $key")
  }


  @ApiAction(desc = "", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeRequest(req: Request): Result<String> {
    return success("ok", "raw request id: " + req.data!!.getInt("id"))
  }


  @ApiAction(desc = "", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeMeta(meta: Meta): Result<String> {
    return success("ok", "raw meta token: " + meta.get("token"))
  }


  @ApiAction(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeFile(doc: Doc): Result<String> {
    return success("ok", doc.content)
  }


  @ApiAction(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeListString(items:List<String>): Result<String> {
    return success("ok", items.fold("", { acc, curr -> acc + "," + curr } ))
  }


  @ApiAction(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeListInt(items:List<Int>): Result<String> {
    return success("ok", items.fold("", { acc, curr -> acc + "," + curr.toString() } ))
  }


  @ApiAction(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeMapInt(items:Map<String,Int>): Result<String> {
    val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
    val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
    return success("ok", delimited)
  }
}

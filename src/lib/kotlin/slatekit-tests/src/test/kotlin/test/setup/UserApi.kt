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
  fun activate(phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): ResultMsg<String> =
    Success("ok", msg ="activated $phone, $code, $isPremiumUser, $date")


  @ApiAction(desc = "activates a users account 3", roles= "@parent", verb = "@parent", protocol = "@parent")
  @ApiArg(name = "phone", desc = "phone number", eg = "123-456-789")
  @ApiArg(name = "code", desc = "activation code", defaultVal = "0", eg = "1234")
  fun testTypes(phone:String, current:Boolean, code:Short, zip:Int, id:Long, rating:Float, value:Double, date: DateTime): ResultMsg<String> =
          Success("ok", msg = "$phone, $current, $code, $zip, $id, $rating, $value, $date")


  @ApiAction(name = "", desc = "activates a users account 3", roles= "", verb = "get", protocol = "@parent")
  fun info(format:String = "json"): ResultMsg<String> {
    return Success("ok", msg ="info")
  }

  
  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "cli")
  fun protocolCLI(code:Int, tag:String): ResultMsg<String> {
    return Success("protocolCLI", msg ="${code} ${tag}")
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "web")
  fun protocolWeb(code:Int, tag:String): ResultMsg<String> {
    return Success("protocolWeb", msg ="${code} ${tag}")
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "*")
  fun protocolAny(code:Int, tag:String): ResultMsg<String> {
    return Success("protocolAny", msg ="${code} ${tag}")
  }


  @ApiAction(name = "", desc = "", roles= "", verb = "@parent", protocol = "@parent")
  fun protocolParent(code:Int, tag:String): ResultMsg<String> {
    return Success("protocolParent", msg ="${code} ${tag}")
  }


  @ApiAction(desc = "", roles= "", verb = "@parent", protocol = "@parent")
  fun testException(code:Int, tag:String): ResultMsg<String> {
    throw Exception("Test unhandled exception")
  }


  @ApiAction(desc = "", roles= "", verb = "@parent", protocol = "@parent")
  fun rolesNone(code:Int, tag:String): ResultMsg<String> {
    return Success("rolesNone", msg ="${code} ${tag}")
  }


  @ApiAction(desc = "", roles= "*", verb = "@parent", protocol = "@parent")
  fun rolesAny(code:Int, tag:String): ResultMsg<String> {
    return Success("rolesAny", msg ="${code} ${tag}")
  }


  @ApiAction(desc = "", roles= "dev", verb = "@parent", protocol = "@parent")
  fun rolesSpecific(code:Int, tag:String): ResultMsg<String>  {
    return Success("rolesSpecific", msg ="${code} ${tag}")
  }


  @ApiAction(desc = "", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun rolesParent(code:Int, tag:String): ResultMsg<String> {
    return Success("rolesParent", msg ="${code} ${tag}")
  }


  @ApiAction(desc = "invites a new user", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun invite(email:String, phone:String, promoCode:String): ResultMsg<String> {
    return Success("ok", msg ="sent invitation to $email, $phone, $promoCode")
  }


  @ApiAction(desc = "invites a new user", roles= "@parent", verb = "@parent", protocol = "@parent")
  fun register(user: User): ResultMsg<String> {
    return Success("ok", msg ="object user")
  }


  @ApiAction(desc = "test decryption of int", roles= "*", verb = "@parent", protocol = "@parent")
  fun decInt(id: EncInt): ResultMsg<String> {
    return Success("ok", msg ="decrypted int : " + id.value)
  }


  @ApiAction(desc = "test decryption of long", roles= "*", verb = "@parent", protocol = "@parent")
  fun decLong(id: EncLong): ResultMsg<String> {
    return Success("ok", msg ="decrypted long : " + id.value)
  }


  @ApiAction(desc = "test decryption of double", roles= "*", verb = "@parent", protocol = "@parent")
  fun decDouble(id: EncDouble): ResultMsg<String>
  {
    return Success("ok", msg ="decrypted double : " + id.value)
  }


  @ApiAction(desc = "test decryption of string", roles= "*", verb = "@parent", protocol = "@parent")
  fun decString(id: EncString): ResultMsg<String>
  {
    return Success("ok", msg ="decrypted string : " + id.value)
  }


  @ApiAction(name = "testArgs", desc = "test types", roles= "*", verb = "@parent", protocol = "@parent")
  fun testArgs(phone:String, code:Int, isPremiumUser:Boolean, date:DateTime, key: EncString): ResultMsg<String>
  {
    return Success("ok", msg ="$phone $code $isPremiumUser, $key")
  }


  @ApiAction(desc = "", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeRequest(req: Request): ResultMsg<String> {
    return Success("ok", msg ="raw request id: " + req.data!!.getInt("id"))
  }


  @ApiAction(desc = "", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeMeta(meta: Meta): ResultMsg<String> {
    return Success("ok", msg ="raw meta token: " + meta.get("token"))
  }


  @ApiAction(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeFile(doc: Doc): ResultMsg<String> {
    return Success("ok", msg =doc.content)
  }


  @ApiAction(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeListString(items:List<String>): ResultMsg<String> {
    return Success("ok", msg =items.fold("", { acc, curr -> acc + "," + curr } ))
  }


  @ApiAction(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeListInt(items:List<Int>): ResultMsg<String> {
    return Success("ok", msg =items.fold("", { acc, curr -> acc + "," + curr.toString() } ))
  }


  @ApiAction(desc = "gets the current promo code", roles= "*", verb = "post", protocol = "@parent")
  fun argTypeMapInt(items:Map<String,Int>): ResultMsg<String> {
    val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
    val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
    return Success("ok", msg =delimited)
  }
}

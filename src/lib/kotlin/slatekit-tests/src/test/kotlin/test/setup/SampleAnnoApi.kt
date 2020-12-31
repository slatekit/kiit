package test.setup

import slatekit.apis.*
import slatekit.apis.AuthModes
import slatekit.common.*
import slatekit.common.auth.Roles
import slatekit.common.crypto.EncDouble
import slatekit.common.crypto.EncInt
import slatekit.common.crypto.EncLong
import slatekit.common.crypto.EncString
import slatekit.common.requests.Request
import slatekit.common.smartvalues.Email
import slatekit.common.smartvalues.PhoneUS
import slatekit.connectors.entities.AppEntContext
import slatekit.results.Notice
import slatekit.results.Success


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs", auth = AuthModes.TOKEN, roles= ["admin"])
class SampleAnnoApi(val context: AppEntContext) {

    @Action(desc = "accepts supplied basic data types from send")
    fun inputBasicTypes(string1: String, bool1: Boolean, numShort: Short, numInt: Int, numLong: Long, numFloat: Float, numDouble: Double, date: DateTime): String {
        return "$string1, $bool1, $numShort $numInt, $numLong, $numFloat, $numDouble, $date"
    }


    @Action(desc = "access the send model directly instead of auto-conversion", roles= [Roles.ALL])
    fun inputRequest(req: Request): Notice<String> {
        return Success("ok", msg = "raw send id: " + req.data!!.getInt("id"))
    }


    @Action(desc = "auto-convert json to objects", roles= [Roles.ALL])
    fun inputObject(movie: Movie): Movie {
        return movie
    }


    @Action(desc = "auto-convert json to objects", roles= [Roles.ALL])
    fun inputObjectlist(movies:List<Movie>): List<Movie> {
        return movies
    }


    @Action(desc = "accepts a list of strings from send", roles= [Roles.ALL])
    fun inputListString(items:List<String>): Notice<String> {
        return Success("ok", msg = items.fold("", { acc, curr -> acc + "," + curr } ))
    }


    @Action(desc = "accepts a list of integers from send", roles= [Roles.ALL])
    fun inputListInt(items:List<Int>): Notice<String> {
        return Success("ok", msg = items.fold("", { acc, curr -> acc + "," + curr.toString() } ))
    }


    @Action(desc = "accepts a map of string/ints from send", roles= [Roles.ALL])
    fun inputMapInt(items:Map<String,Int>): Notice<String> {
        val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
        val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
        return Success("ok", msg = delimited)
    }


    @Action(desc = "accepts an encrypted int that will be decrypted", roles= [Roles.ALL])
    fun inputDecInt(id: EncInt): Notice<String> {
        return Success("ok", msg ="decrypted int : " + id.value)
    }


    @Action(desc = "accepts an encrypted long that will be decrypted", roles= [Roles.ALL])
    fun inputDecLong(id: EncLong): Notice<String> {
        return Success("ok", msg ="decrypted long : " + id.value)
    }


    @Action(desc = "accepts an encrypted double that will be decrypted", roles= [Roles.ALL])
    fun inputDecDouble(id: EncDouble): Notice<String>
    {
        return Success("ok", msg = "decrypted double : " + id.value)
    }


    @Action(desc = "accepts an encrypted string that will be decrypted", roles= [Roles.ALL])
    fun inputDecString(id: EncString): Notice<String>
    {
        return Success("ok", msg = "decrypted string : " + id.value)
    }


    @Action(desc = "accepts a smart string of phone", roles= [Roles.GUEST])
    fun smartStringPhone(text: PhoneUS): String = "${text.value}"


    @Action(desc = "accepts a smart string of email", roles= [Roles.GUEST])
    fun smartStringEmail(text: Email): String = "${text.value}"


}

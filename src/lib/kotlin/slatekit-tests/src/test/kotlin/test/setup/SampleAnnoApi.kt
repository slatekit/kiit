package test.setup

import slatekit.apis.*
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.*
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.encrypt.EncString
import slatekit.common.requests.Request
import slatekit.common.smartvalues.Email
import slatekit.common.smartvalues.PhoneUS
import slatekit.integration.common.AppEntContext
import slatekit.results.Notice
import slatekit.results.Success


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs", auth = AuthModes.token, roles= "admin", verb = Verbs.auto, protocol = Protocols.all)
class SampleAnnoApi(val context: AppEntContext) {

    @ApiAction(desc = "accepts supplied basic data types from request", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun inputBasicTypes(string1: String, bool1: Boolean, numShort: Short, numInt: Int, numLong: Long, numFloat: Float, numDouble: Double, date: DateTime): String {
        return "$string1, $bool1, $numShort $numInt, $numLong, $numFloat, $numDouble, $date"
    }


    @ApiAction(desc = "access the request model directly instead of auto-conversion", roles= "*", verb = "post", protocol = "@parent")
    fun inputRequest(req: Request): Notice<String> {
        return Success("ok", msg = "raw request id: " + req.data!!.getInt("id"))
    }


    @ApiAction(desc = "auto-convert json to objects", roles= "*", verb = "post", protocol = "@parent")
    fun inputObject(movie: Movie): Movie {
        return movie
    }


    @ApiAction(desc = "auto-convert json to objects", roles= "*", verb = "post", protocol = "@parent")
    fun inputObjectlist(movies:List<Movie>): List<Movie> {
        return movies
    }


    @ApiAction(desc = "accepts a list of strings from request", roles= "*", verb = "post", protocol = "@parent")
    fun inputListString(items:List<String>): Notice<String> {
        return Success("ok", msg = items.fold("", { acc, curr -> acc + "," + curr } ))
    }


    @ApiAction(desc = "accepts a list of integers from request", roles= "*", verb = "post", protocol = "@parent")
    fun inputListInt(items:List<Int>): Notice<String> {
        return Success("ok", msg = items.fold("", { acc, curr -> acc + "," + curr.toString() } ))
    }


    @ApiAction(desc = "accepts a map of string/ints from request", roles= "*", verb = "post", protocol = "@parent")
    fun inputMapInt(items:Map<String,Int>): Notice<String> {
        val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
        val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
        return Success("ok", msg = delimited)
    }


    @ApiAction(desc = "accepts an encrypted int that will be decrypted", roles= "*", verb = "@parent", protocol = "@parent")
    fun inputDecInt(id: EncInt): Notice<String> {
        return Success("ok", msg ="decrypted int : " + id.value)
    }


    @ApiAction(desc = "accepts an encrypted long that will be decrypted", roles= "*", verb = "@parent", protocol = "@parent")
    fun inputDecLong(id: EncLong): Notice<String> {
        return Success("ok", msg ="decrypted long : " + id.value)
    }


    @ApiAction(desc = "accepts an encrypted double that will be decrypted", roles= "*", verb = "@parent", protocol = "@parent")
    fun inputDecDouble(id: EncDouble): Notice<String>
    {
        return Success("ok", msg = "decrypted double : " + id.value)
    }


    @ApiAction(desc = "accepts an encrypted string that will be decrypted", roles= "*", verb = "@parent", protocol = "@parent")
    fun inputDecString(id: EncString): Notice<String>
    {
        return Success("ok", msg = "decrypted string : " + id.value)
    }


    @ApiAction(desc = "accepts a smart string of phone", roles= "?", verb = "@parent", protocol = "@parent")
    fun smartStringPhone(text: PhoneUS): String = "${text.value}"


    @ApiAction(desc = "accepts a smart string of email", roles= "?", verb = "@parent", protocol = "@parent")
    fun smartStringEmail(text: Email): String = "${text.value}"


}

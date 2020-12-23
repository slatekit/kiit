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

package test.apis.samples

import slatekit.apis.*
import slatekit.common.*
import slatekit.common.types.Doc
import slatekit.common.requests.Request
import slatekit.common.checks.Check
import slatekit.context.Context
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

@Api(area = "samples", name = "core", desc = "api to access and manage users 3", auth = AuthModes.NONE)
class Sample_API_1_Core(context: Context) {


    @Action(desc = "processes an request with 0 parameters")
    fun processEmpty(): Outcome<String> {
        return Success("ok", msg = "no inputs")
    }


    private fun processPrivate(): Outcome<String> {
        return Success("ok", msg = "this is private")
    }


    @Action(name = "checkName", desc = "processes action with name different than method")
    fun processExplicitName(name:String): Outcome<String> {
        return Success("ok", msg = "$name ok")
    }


    @Action(desc = "activates a users account 3")
    fun processInputs(phone:String, code:Int, isOn:Boolean, date: DateTime): Outcome<String> {
        return Success("ok", msg = "inputs $phone, $code, $isOn, $date")
    }


    @Action(desc = "processes a request with raw slatekit Request type")
    fun processRequest(req: Request): Outcome<String> {
        return Success("ok", msg = "raw send id: " + req.data.getInt("id"))
    }


    @Action(desc = "processes a request with raw slatekit Meta type")
    fun processMeta(meta: Metadata): Outcome<String> {
        return Success("ok", msg = "raw meta token: " + meta.get("token"))
    }


    @Action(desc = "processes with an Document type")
    fun processFile(doc: Doc): Outcome<String> {
        return Success("ok", msg = doc.content)
    }


    @Action(desc = "gets the current promo code")
    fun processInputListString(items: List<String>): Outcome<String> {
        return Success("ok", msg = items.fold("", { acc, curr -> acc + "," + curr }))
    }


    @Action(desc = "gets the current promo code")
    fun processInputListInt(items: List<Int>): Outcome<String> {
        return Success("ok", msg = items.fold("", { acc, curr -> acc + "," + curr.toString() }))
    }


    @Action(desc = "gets the current promo code")
    fun processInputMap(items: Map<String, Int>): Outcome<String> {
        val sortedPairs = items.keys.toList().sortedBy { k: String -> k }.map { key -> Pair(key, items[key]) }
        val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second })
        return Success("ok", msg = delimited)
    }


    @Action(desc = "process with erorr")
    fun processError(text:String): Outcome<Int> {
        return if(text.isNullOrEmpty()) {
            Outcomes.invalid("You must supply a non-empty string")
        }
        else if(!Check.isNumeric(text)){
            Outcomes.errored("$text is not a valid number")
        }
        else {
            Outcomes.success(text.toInt(), msg = "You supplied a valid number")
        }
    }
}

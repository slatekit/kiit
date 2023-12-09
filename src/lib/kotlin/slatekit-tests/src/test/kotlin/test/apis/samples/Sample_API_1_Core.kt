/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package test.apis.samples

import kiit.apis.*
import kiit.apis.core.Transformer
import kiit.common.*
import kiit.common.types.ContentFile
import kiit.requests.Request
import kiit.common.checks.Check
import kiit.common.values.Metadata
import kiit.context.Context
import kiit.meta.JSONRestoreWithContext
import kiit.meta.JSONTransformer
import kiit.results.Outcome
import kiit.results.Success
import kiit.results.builders.Outcomes
import org.json.simple.JSONObject

@Api(area = "samples", name = "core", desc = "api to access and manage users 3", auth = AuthModes.NONE)
class Sample_API_1_Core(context: Context) {


    @Action( desc = "processes an request with 0 parameters")
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


    @Action(desc = "processes a request with raw request, meta")
    fun processBoth(req:Request, meta: Metadata): Outcome<String> {
        return Success("ok", msg = "raw both")
    }


    @Action(desc = "processes a request with raw request, meta")
    fun processCustom(req:Request, custom1:Custom1, custom2: Custom2, note:String): Outcome<String> {
        return Success("custom1=${custom1.name}, custom2=${custom2.code}, note=$note", msg = "raw both")
    }


    @Action(desc = "processes with an Document type")
    fun processFile(doc: ContentFile): Outcome<String> {
        return Success("ok", msg = String(doc.data))
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

data class Custom1(val name:String)
data class Custom2(val code:Int)

class Custom1Decoder : Transformer<Custom1>(Custom1::class.java), JSONRestoreWithContext<Custom1> {
    override fun <T> restore(ctx: T, model: JSONObject?, key:String): Custom1? {
        val req = ctx as Request
        val name = req.meta.getString("name")
        return Custom1(name)
    }
}


class Custom2Decoder : Transformer<Custom2>(Custom2::class.java), JSONRestoreWithContext<Custom2> {
    override fun <T> restore(ctx: T, model: JSONObject?, key:String): Custom2? {
        val req = ctx as Request
        val code = req.meta.getInt("code")
        return Custom2(code)
    }
}

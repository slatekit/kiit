package slatekit.serialization.errors

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.results.*
import slatekit.results.builders.Outcomes

/**
 * {
    "errs": [{
    "field": "email",
    "type": "input",
    "message": "Email already exists",
    "value": "slate.kit@gmail.com"
    }],
    "code": 500004,
    "success": false,
    "meta": null,
    "name": "CONFLICT",
    "tag": null,
    "value": null,
    "desc": "Conflict"
    }
 */
object OutcomeEncoder {

    fun <T> encode(data: T?): String? {
        TODO("Not yet implemented")
    }

    fun <T> decode(json: String?, cls:Class<*>, converter:(JSONObject) -> T): Outcome<T> {
        val parser = JSONParser()
        val doc = parser.parse(json)
        val root = doc as JSONObject
        val result = when(root.get("success") as Boolean) {
            true -> {
                val status = decodeStatus(root) as Passed
                val value = converter(root)
                Success(value, status)
            }
            false -> {
                if (root.containsKey("errs")) {
                    val errList = decodeErrs(root)
                    Outcomes.errored<T>(errList)
                } else {
                    Outcomes.errored<T>("unable to parse result")
                }
            }
        }
        return result
    }


    fun decodeStatus(root:JSONObject): Status {
        val code = (root.get("code") as Long).toInt()
        val status = when(val s = Codes.toStatus(code)){
            null -> {
                val name = root.get("name") as String
                val desc = root.get("desc") as String
                val type = root.get("type") as String
                when(type) {
                    "Succeeded" -> Passed.Succeeded(name, code, desc)
                    "Pending"   -> Passed.Pending(name, code, desc)
                    "Denied"   -> Failed.Denied (name, code, desc)
                    "Ignored"   -> Failed.Ignored(name, code, desc)
                    "Invalid"   -> Failed.Invalid(name, code, desc)
                    "Errored"   -> Failed.Errored(name, code, desc)
                    else        -> Failed.Unknown(name, code, desc)
                }
            }
            else -> s
        }
        return status
    }


    fun decodeErrs(root:JSONObject): Err.ErrorList {
        val errs = when(val errs = root.get("errs")) {
            is JSONArray -> {
                errs.mapNotNull { err ->
                    when(err) {
                        is JSONObject -> decodeErr(err)
                        else -> null
                    }
                }
            }
            else -> listOf()
        }
        return Err.ErrorList(errs, if(errs.isNotEmpty()) errs[0].msg else "")
    }


    fun decodeErr(err:JSONObject): Err {
        val type = err.getValue("type")
        return when(type?.toString()?.trim()){
            "input"  -> {
                Err.on(
                        field = err.getValue("field") as String,
                        value = err.getValue("value") as String,
                        msg = err.getValue("message") as String
                )
            }
            "action" -> {
                Err.of(msg = err.getValue("message") as String)
            }
            else -> Err.of(msg = err.getValue("message") as String)
        }
    }
}
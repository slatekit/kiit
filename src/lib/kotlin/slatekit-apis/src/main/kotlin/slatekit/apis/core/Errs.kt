package slatekit.apis.core

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import slatekit.common.requests.Response
import slatekit.results.Err
import slatekit.results.ErrorField
import slatekit.results.ErrorInfo
import slatekit.results.ErrorList

object Errs {

    /**
     * Builds a Slate Kit standardized response object
     * @sample
     * {
     *      "success": false,
     *      "code": 400003,
     *      "msg": "Invalid",
     *      "errs": [
     *          { "type": "input", "field": "name", "value": ""   , "message": "Missing"       },
     *          { "type": "input", "field": "dept", "value": "abc", "message": "Invalid value" }
     *      ],
     *      "meta": null,
     *      "tag": null,
     *      "value": null
     *   }
     */
    fun response(err: Err, result: Response<Any>):JSONObject{
        val errors = JSONArray()
        flatten(err, errors, 0)
        val json = JSONObject()
        json["success"] = false
        json["code"] = result.code
        json["meta"] = result.meta
        json["value"] = null
        json["msg"] = result.msg
        json["errs"] = errors
        json["tag"] = result.tag
        return json
    }


    /**
     * Recursively flattens the err into the list provided.
     * E.g. Err could be
     * 1. @see[slatekit.results.ErrorInfo]
     * 2. @see[slatekit.results.ErrorField]
     * 3. @see[slatekit.results.ErrorList]
     */
    fun flatten(error: Err, list: JSONArray, depth: Int) {
        when (error) {
            is ErrorField -> {
                build("input", error.field, error.value, error.msg, list)
            }
            is ErrorInfo -> {
                build("action", null, null, error.msg, list)
            }
            is ErrorList -> {
                if (depth < 3) {
                    val errs = error.errors
                    errs.forEach {
                        flatten(it, list, depth + 1)
                    }
                }
            }
        }
    }


    /**
     * Builds a single JSON error using the info supplied.
     * NOTE: All errors are "normalized" into a type, field, value, message
     * @param type: Type of the error e.g. "field" for field level errors
     * @param field: Name of the field ( supply null if not on a field )
     * @param value: Value causing the failure
     * @param msg  : Message describing the error in detail
     * @param list : List to add the created error into
     */
    fun build(type: String, field: String?, value: String?, msg: String?, list: JSONArray) {
        val err = JSONObject()
        err["type"] = type
        err["field"] = field
        err["value"] = value
        err["message"] = msg
        list.add(err)
    }
}

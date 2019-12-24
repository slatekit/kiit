package slatekit.apis.core

import org.json.simple.JSONObject
import slatekit.meta.JSONTransformer


/**
 * Transform to convert, encode a type to/from a JSON Object or JSON String
 */
open class Transformer<S>(override val cls: Class<*>,
                     val convertOp:((data:S?, type: Class<*>) -> JSONObject?)? = null ,
                     val restoreOp:((doc:JSONObject?, type: Class<*>) -> S?)? = null )
    : JSONTransformer<S> {


    override fun convert(input: S?): JSONObject? {
        return convertOp?.invoke(input, cls)
    }

    override fun restore(output: JSONObject?): S? {
        return restoreOp?.invoke(output, cls)
    }


//    /**
//     * Enriched method with access to request
//     */
//    fun convert(req:ApiRequest, input: S?): JSONObject? {
//        return convert(input)
//    }
//
//    /**
//     * Enriched method with access to request
//     */
//    fun restore(req:ApiRequest, output: JSONObject?): S? {
//        return restore(output)
//    }
}

package kiit.apis.core

import org.json.simple.JSONObject
import kiit.meta.JSONTransformer


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
}

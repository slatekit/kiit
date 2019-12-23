package slatekit.apis.core

import org.json.simple.JSONObject
import slatekit.apis.support.TransformSupport
import slatekit.common.Converter
import slatekit.common.Encoder


/**
 * Transform to convert, encode a type to/from a JSON Object or JSON String
 */
open class Transformer<S>(override val cls: Class<*>,
                     val convertOp:((data:S?, type: Class<*>) -> JSONObject?)? = null ,
                     val restoreOp:((doc:JSONObject?, type: Class<*>) -> S?)? = null )
    : TransformSupport<S>, Converter<S, JSONObject>, Encoder<S, String> {

    override val isConvertable: Boolean = convertOp != null

    override val isRestorable: Boolean = restoreOp != null

    override fun convert(input: S?): JSONObject? {
        return convertOp?.invoke(input, cls)
    }

    override fun restore(output: JSONObject?): S? {
        return restoreOp?.invoke(output, cls)
    }
}

package slatekit.meta

import org.json.simple.JSONObject
import slatekit.common.requests.Request
import kotlin.reflect.KClass
import kotlin.reflect.KType



interface Encoder {
    /**
     * The data type associated with this encoder
     */
    val type: KType

    /**
     * Qualified fname of the type
     */
    val name:String


    /**
     * Encodes the value to a JSON object.
     */
    fun encode(data: Any?): JSONObject?
}


interface Decoder {

    /**
     * The data type associated with this encoder
     */
    val type: KType

    /**
     * Qualified fname of the type
     */
    val name:String

    /**
     * Decodes
     */
    fun decode(request: Request, doc:JSONObject, type: KType):Any?
}


abstract class SimplerDecoder(override val type: KType) : Decoder {

    /**
     * Qualified name of decoder
     */
    override val name :String by lazy { (type.classifier as KClass<*>).qualifiedName ?: "" }
}


class LambdaDecoder(override val type: KType, val op:(request: Request, doc:JSONObject, type: KType) -> Any?) : Decoder {

    /**
     * Qualified name of decoder
     */
    override val name :String by lazy { (type.classifier as KClass<*>).qualifiedName ?: "" }


    override fun decode(request: Request, doc:JSONObject, type: KType):Any? {
        return op(request, doc, type)
    }
}
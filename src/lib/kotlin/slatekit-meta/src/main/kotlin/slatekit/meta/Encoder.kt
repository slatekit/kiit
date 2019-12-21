package slatekit.meta

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.common.Converter
import slatekit.common.requests.Request
import kotlin.reflect.KClass
import kotlin.reflect.KType



interface Encoder<S,T> : Converter<S,T> {
    /**
     * Decodes
     */
    fun encode(data:S?):String?


    /**
     * Decodes
     */
    fun decode(item:String?):S?
}


abstract class JSONEncoder<S>(val type: KType) : Encoder<S,JSONObject> {

    /**
     * Qualified name of decoder
     */
    override val name :String = (type.classifier as KClass<*>).qualifiedName ?: ""

    /**
     * Encodes the item to a JSON string or null
     */
    override fun encode(data:S?):String? {
        val json= convert(data)
        return json?.toJSONString()
    }

    /**
     * Decodes JSON string to item or null
     */
    override fun decode(item:String?):S? {
        return item?.let {
            val json = JSONParser()
            val jsonItem = json.parse(it)
            when(jsonItem) {
                is JSONObject -> restore(jsonItem)
                else          -> null
            }
        }
    }
}



class LambdaDecoder<S,T>(val type: KType, val op:(request: Request, doc:JSONObject, type: KType) -> Any?) : Encoder<S,T> {

    /**
     * Qualified name of decoder
     */
    override val name :String = (type.classifier as KClass<*>).qualifiedName ?: ""


    override fun decode(request: Request, doc:JSONObject, type: KType):Any? {
        return op(request, doc, type)
    }
}
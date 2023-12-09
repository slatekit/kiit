package kiit.serialization.deserializer.json

import kiit.common.crypto.Encryptor
import kiit.serialization.deserializer.DecodeSupport
import kiit.serialization.deserializer.Decoder
import org.json.simple.JSONObject
import kotlin.reflect.KType


class JsonSmartValueDecoder(private val converter: JsonConverter,
                             private val enc: Encryptor?) : Decoder<JSONObject>, DecodeSupport {

    override fun decode(context:Any, parent:JSONObject, paramName:String, paramValue:Any?, paramType: KType):Any? {
        val result = handle(paramValue, null) { converter.toSmartValue(paramValue?.toString() ?: "", paramName, paramType) }
        return result
    }
}
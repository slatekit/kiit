package kiit.serialization.deserializer.json

import kiit.common.crypto.Encryptor
import kiit.meta.Reflector
import kiit.serialization.deserializer.DecodeSupport
import kiit.serialization.deserializer.Decoder
import org.json.simple.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KType

class JsonEnumDecoder(
    private val converter: JsonConverter,
    private val enc: Encryptor?
) : Decoder<JSONObject>, DecodeSupport {

    override fun decode(parent: Any, paramName: String, paramValue: Any?, paramType: KType): Any? {
        val cls = paramType.classifier as KClass<*>
        val result = Reflector.getEnumValue(cls, paramValue)
        return result
    }
}
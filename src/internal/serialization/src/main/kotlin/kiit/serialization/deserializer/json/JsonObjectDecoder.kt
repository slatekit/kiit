package kiit.serialization.deserializer.json

import kiit.common.crypto.Encryptor
import kiit.serialization.deserializer.DecodeSupport
import kiit.serialization.deserializer.Decoder
import org.json.simple.JSONObject
import kotlin.reflect.KType


class JsonObjectDecoder(
    private val converter: JsonConverter,
    private val enc: Encryptor?
) : Decoder<JSONObject>, DecodeSupport {

    override fun decode(parent: Any, paramName: String, paramValue: Any?, paramType: KType): Any? {
        return when (paramValue) {
            is JSONObject -> converter.toObject(paramValue, paramName, paramType)
            else -> handle(paramValue, null) { null }
        }
    }
}
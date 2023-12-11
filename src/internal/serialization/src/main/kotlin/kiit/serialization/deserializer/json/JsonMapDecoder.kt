package kiit.serialization.deserializer.json

import kiit.common.crypto.Encryptor
import kiit.serialization.deserializer.DecodeSupport
import kiit.serialization.deserializer.Decoder
import org.json.simple.JSONObject
import kotlin.reflect.KType


class JsonMapDecoder(
    private val converter: JsonConverter,
    private val enc: Encryptor?
) : Decoder<JSONObject>, DecodeSupport {

    override fun decode(parent: Any, paramName: String, paramValue: Any?, paramType: KType): Any? {
        val tpeKey = paramType.arguments[0].type!!
        val tpeVal = paramType.arguments[1].type!!
        val emptyMap = mapOf<Any, Any>()
        val items = when (paramValue) {
            is JSONObject -> converter.toMap(paramValue, paramName, tpeKey, tpeVal)
            else -> handle(paramValue, emptyMap) { emptyMap } as Map<*, *>
        }
        return items
    }
}
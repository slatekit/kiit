package kiit.serialization.deserializer.json

import kiit.common.crypto.Encryptor
import kiit.serialization.deserializer.DecodeSupport
import kiit.serialization.deserializer.Decoder
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import kotlin.reflect.KType


class JsonListDecoder(
    private val converter: JsonConverter,
    private val enc: Encryptor?
) : Decoder<JSONObject>, DecodeSupport {

    override fun decode(parent: Any, paramName: String, paramValue: Any?, paramType: KType): Any? {
        val listType = paramType.arguments[0]!!.type!!
        return when (paramValue) {
            is JSONArray -> converter.toList(paramValue, paramName, listType)
            else -> handle(paramValue, listOf<Any>()) { listOf<Any>() } as List<*>
        }
    }
}
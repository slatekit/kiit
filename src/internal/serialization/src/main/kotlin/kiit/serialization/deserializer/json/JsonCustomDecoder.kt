package kiit.serialization.deserializer.json

import kiit.common.crypto.Encryptor
import kiit.meta.JSONRestoreWithContext
import kiit.meta.JSONTransformer
import kiit.serialization.deserializer.DecodeSupport
import kiit.serialization.deserializer.Decoder
import org.json.simple.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KType


class JsonCustomDecoder(
    private val converter: JsonConverter,
    private val enc: Encryptor?,
    private val decoders: Map<String, JSONTransformer<*>> = mapOf()
) : Decoder<JSONObject>, DecodeSupport {

    override fun decode(context: Any, parent: Any, paramName: String, paramValue: Any?, paramType: KType): Any? {
        val cls = paramType.classifier as KClass<*>
        val fullName = cls.qualifiedName
        val decoder = decoders[fullName]
        val json:JSONObject? = when (paramValue) {
            is JSONObject -> paramValue
            else -> parent as? JSONObject
        }
        val result = when (decoder) {
            is JSONRestoreWithContext<*> -> {
                decoder.restore(context, json, paramName)
            }

            else -> {
                decoder?.restore(json)
            }
        }
        return result
    }
}
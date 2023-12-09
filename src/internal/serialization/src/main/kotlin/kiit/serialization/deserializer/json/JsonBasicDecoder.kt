@file:Suppress("IMPLICIT_CAST_TO_ANY")

package kiit.serialization.deserializer.json

import kiit.common.convert.Conversions
import kiit.common.crypto.*
import kiit.meta.KTypes
import kiit.requests.Request
import kiit.serialization.deserializer.*
import org.json.simple.JSONObject
import java.util.*
import kotlin.reflect.KType


class SimpleDecoder(val op:DecodeFunction) : Decoder<JSONObject> {
    override fun decode(context: Any, parent: JSONObject, paramName: String, paramValue: Any?, paramType: KType): Any? {
        return op(parent, paramName, paramValue, paramType)
    }
}

class JsonBasicDecoder(
    private val converter: JsonConverter,
    private val enc: Encryptor?
) : Decoder<JSONObject>, DecodeSupport {

    override fun decode(context: Any, parent: JSONObject, paramName: String, paramValue: Any?, paramType: KType): Any? {
        val result = when (paramType.classifier) {
            // Basic
            KTypes.KStringClass -> paramValue?.let { Conversions.handleString(it) }
            KTypes.KBoolClass -> paramValue?.toString()?.toBoolean()
            KTypes.KShortClass -> paramValue?.toString()?.toShort()
            KTypes.KIntClass -> paramValue?.toString()?.toInt()
            KTypes.KLongClass -> paramValue?.toString()?.toLong()
            KTypes.KFloatClass -> paramValue?.toString()?.toFloat()
            KTypes.KDoubleClass -> paramValue?.toString()?.toDouble()

            // Dates
            KTypes.KLocalDateClass -> paramValue?.let { Conversions.toLocalDate(it as String) }
            KTypes.KLocalTimeClass -> paramValue?.let { Conversions.toLocalTime(it as String) }
            KTypes.KLocalDateTimeClass -> paramValue?.let { Conversions.toLocalDateTime(it as String) }
            KTypes.KZonedDateTimeClass -> paramValue?.let { Conversions.toZonedDateTime(it as String) }
            KTypes.KDateTimeClass -> paramValue?.let { Conversions.toDateTime(it as String) }

            // Encrypted
            KTypes.KDecIntClass -> enc?.let { e -> EncInt(paramValue as String, e.decrypt(paramValue).toInt()) } ?: EncInt("", 0)
            KTypes.KDecLongClass -> enc?.let { e -> EncLong(paramValue as String, e.decrypt(paramValue).toLong()) } ?: EncLong("", 0L)
            KTypes.KDecDoubleClass -> enc?.let { e -> EncDouble(paramValue as String, e.decrypt(paramValue).toDouble()) } ?: EncDouble("", 0.0)
            KTypes.KDecStringClass -> enc?.let { e -> EncString(paramValue as String, e.decrypt(paramValue)) } ?: EncString("", "")

            // Kiit
            KTypes.KVarsClass -> paramValue?.let { Conversions.toVars(it) }
            KTypes.KDocClass  -> converter.toDoc(context as Request, paramName)
            KTypes.KUUIDClass -> UUID.fromString(paramValue.toString())

            else -> null
        }
        return result
    }
}
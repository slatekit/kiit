package slatekit.serialization.deserializer

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import slatekit.common.convert.Conversions
import slatekit.common.crypto.*
import slatekit.requests.Request
import slatekit.meta.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType


class Deserializers(conversion: Conversion, enc: Encryptor?, decoders:Map<String, JSONTransformer<*>>) {
    val basic  = BasicDeserializer(conversion, enc)
    val lists  = ListDeserializer(conversion, enc)
    val maps   = MapDeserializer(conversion, enc)
    val enums  = EnumDeserializer(conversion, enc)
    val smart  = SmartValueDeserializer(conversion, enc)
    val objs   = ObjectDeserializer(conversion, enc)
    val custom = CustomDeserializer(conversion, enc, decoders)
}


class BasicDeserializer(override val conversion: Conversion,
                        override val enc: Encryptor?) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val result = when (paramType.classifier) {

            // Basic
            KTypes.KStringType.classifier -> paramValue?.let { Conversions.handleString(it) }
            KTypes.KBoolType.classifier -> paramValue?.toString()?.toBoolean()
            KTypes.KShortType.classifier -> paramValue?.toString()?.toShort()
            KTypes.KIntType.classifier -> paramValue?.toString()?.toInt()
            KTypes.KLongType.classifier -> paramValue?.toString()?.toLong()
            KTypes.KFloatType.classifier -> paramValue?.toString()?.toFloat()
            KTypes.KDoubleType.classifier -> paramValue?.toString()?.toDouble()

            // Dates
            KTypes.KLocalDateType.classifier -> paramValue?.let { Conversions.toLocalDate(it as String) }
            KTypes.KLocalTimeType.classifier -> paramValue?.let { Conversions.toLocalTime(it as String) }
            KTypes.KLocalDateTimeType.classifier -> paramValue?.let { Conversions.toLocalDateTime(it as String) }
            KTypes.KZonedDateTimeType.classifier -> paramValue?.let { Conversions.toZonedDateTime(it as String) }
            KTypes.KDateTimeType.classifier -> paramValue?.let { Conversions.toDateTime(it as String) }

            // Encrypted
            KTypes.KDecIntType.classifier -> enc?.let { e -> EncInt(paramValue as String, e.decrypt(paramValue).toInt()) } ?: EncInt("", 0)
            KTypes.KDecLongType.classifier -> enc?.let { e -> EncLong(paramValue as String, e.decrypt(paramValue).toLong()) } ?: EncLong("", 0L)
            KTypes.KDecDoubleType.classifier -> enc?.let { e -> EncDouble(paramValue as String, e.decrypt(paramValue).toDouble()) } ?: EncDouble("", 0.0)
            KTypes.KDecStringType.classifier -> enc?.let { e -> EncString(paramValue as String, e.decrypt(paramValue)) } ?: EncString("", "")

            // Slate Kit
            KTypes.KVarsType.classifier -> paramValue?.let { Conversions.toVars(it) }
            KTypes.KDocType.classifier  -> conversion.toDoc(context as Request, paramName)
            KTypes.KUUIDType.classifier -> UUID.fromString(paramValue.toString())

            else -> null
        }
        return result
    }
}


class ListDeserializer(override val conversion: Conversion,
                       override val enc: Encryptor?) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val listType = paramType.arguments[0]!!.type!!
        return when (paramValue) {
            is JSONArray -> conversion.toList(paramValue, paramName, listType)
            else -> handle(paramValue, listOf<Any>()) { listOf<Any>() } as List<*>
        }
    }
}


class MapDeserializer(override val conversion: Conversion,
                      override val enc: Encryptor?) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val tpeKey = paramType.arguments[0].type!!
        val tpeVal = paramType.arguments[1].type!!
        val emptyMap = mapOf<Any, Any>()
        val items = when (paramValue) {
            is JSONObject -> conversion.toMap(paramValue, paramName, tpeKey, tpeVal)
            else -> handle(paramValue, emptyMap) { emptyMap } as Map<*, *>
        }
        return items
    }
}


class EnumDeserializer(override val conversion: Conversion,
                       override val enc: Encryptor?) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val cls = paramType.classifier as KClass<*>
        val result = Reflector.getEnumValue(cls, paramValue)
        return result
    }
}


class SmartValueDeserializer(override val conversion: Conversion,
                             override val enc: Encryptor?) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val result = handle(paramValue, null) { conversion.toSmartValue(paramValue?.toString() ?: "", paramName, paramType) }
        return result
    }
}


class ObjectDeserializer(override val conversion: Conversion,
                         override val enc: Encryptor?) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        return when (paramValue) {
            is JSONObject -> conversion.toObject(paramValue, paramName, paramType)
            else -> handle(paramValue, null) { null }
        }
    }
}


class CustomDeserializer(override val conversion: Conversion,
                         override val enc: Encryptor?,
                         val decoders: Map<String, JSONTransformer<*>> = mapOf()) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val cls = paramType.classifier as KClass<*>
        val fullName = cls.qualifiedName
        val decoder = decoders[fullName]
        val json = when(paramValue) {
            is JSONObject -> paramValue
            else -> parent as JSONObject
        }
        val result = when(decoder) {
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
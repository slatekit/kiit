package slatekit.meta.deserializer

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import slatekit.meta.Conversion
import slatekit.meta.JSONRestoreWithContext
import slatekit.meta.JSONTransformer
import slatekit.meta.Reflector
import kotlin.reflect.KClass
import kotlin.reflect.KType


class Deserializers(conversion: Conversion, decoders:Map<String, JSONTransformer<*>>) {
    val lists  = ListDeserializer(conversion)
    val maps   = MapDeserializer(conversion)
    val enums  = EnumDeserializer(conversion)
    val smart  = SmartValueDeserializer(conversion)
    val objs   = ObjectDeserializer(conversion)
    val custom = CustomDeserializer(conversion, decoders)
}


class ListDeserializer(override val conversion: Conversion) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val listType = paramType.arguments[0]!!.type!!
        return when (paramValue) {
            is JSONArray -> conversion.toList(paramValue, paramName, listType)
            else -> handle(paramValue, listOf<Any>()) { listOf<Any>() } as List<*>
        }
    }
}


class MapDeserializer(override val conversion: Conversion) : DeserializerPart, DeserializeSupport {

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


class EnumDeserializer(override val conversion: Conversion) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val cls = paramType.classifier as KClass<*>
        val result = Reflector.getEnumValue(cls, paramValue)
        return result
    }
}


class SmartValueDeserializer(override val conversion: Conversion) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        val result = handle(paramValue, null) { conversion.toSmartValue(paramValue?.toString() ?: "", paramName, paramType) }
        return result
    }
}


class ObjectDeserializer(override val conversion: Conversion) : DeserializerPart, DeserializeSupport {

    override fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any? {
        return when (paramValue) {
            is JSONObject -> conversion.toObject(paramValue, paramName, paramType)
            else -> handle(paramValue, null) { null }
        }
    }
}


class CustomDeserializer(override val conversion: Conversion,
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
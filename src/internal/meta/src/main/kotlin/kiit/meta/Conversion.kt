package kiit.meta

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import kiit.common.convert.Conversions
import kiit.requests.Request
import kiit.requests.RequestSupport
import kiit.utils.smartvalues.SmartCreation
import kiit.utils.smartvalues.SmartValue
import kiit.common.types.ContentFile
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.companionObjectInstance

class Conversion(val converter: (parent: Any, raw: Any?, paramName:String, paramType: KType) -> Any?) {

    fun toList(source: JSONArray, name:String, tpe: KType): List<*> {
        val items = source.map { item ->
            item?.let { jsonItem -> converter(source, jsonItem, name, tpe) }
        }.filterNotNull()
        return items
    }

    fun toMap(source: JSONObject, name:String, tpeKey: KType, tpeVal: KType): Map<*, *> {
        val keyConverter = Conversions.converterFor(tpeKey.javaClass)
        val items = source.map { entry ->
            val key = keyConverter(entry.key?.toString()!!)
            val keyVal = converter(source, entry.value, name, tpeVal)
            Pair(key, keyVal)
        }.filterNotNull().toMap()
        return items
    }

    fun toObject(source: JSONObject, name:String, tpe: KType): Any {
        val cls = tpe.classifier as KClass<*>
        val props = Reflector.getProperties(cls)
        val items = props.map { prop ->
            val raw = source.get(prop.name)
            val converted = converter(source, raw, name, prop.returnType)
            converted
        }
        val instance = Reflector.createWithArgs<Any>(cls, items.toTypedArray())
        return instance
    }

    fun toSmartValue(source: String, name:String, tpe: KType): SmartValue {

        val cls = tpe.classifier as KClass<*>
        val creator = cls.companionObjectInstance as SmartCreation<*>
        val result = creator.of(source)
        return result
    }

    fun toDoc(req:Request, name:String): ContentFile? {
        // Conversions.toDoc(data.getString(paramName))
        val doc = when(req){
            is RequestSupport -> req.getDoc(name)
            else -> null
        }
        return doc
    }
}
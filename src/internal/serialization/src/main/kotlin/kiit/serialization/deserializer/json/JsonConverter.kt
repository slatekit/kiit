package kiit.serialization.deserializer.json

import kiit.common.convert.Conversions
import kiit.common.types.ContentFile
import kiit.meta.Reflector
import kiit.requests.Request
import kiit.requests.RequestSupport
import kiit.utils.smartvalues.SmartCreation
import kiit.utils.smartvalues.SmartValue
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.companionObjectInstance


class JsonConverter(val defaults:Map<String, Any>,
                    val converter: (parent: Any, raw: Any?, paramName:String, paramType: KType) -> Any?) {


    // Stores a cache of class names to properties with default values.
    // e.g. "myapp.Task" -> { title: "abc", "priority" : 1 }
    private val cache = mapOf<String, Map<String, Any>>()

    fun toList(source: JSONArray, name:String, tpe: KType): List<*> {
        val items = source.mapNotNull { item ->
            item?.let { jsonItem -> converter(source, jsonItem, name, tpe) }
        }
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
            val converted = when(raw) {
                null -> getNullOrDefault(cls, prop)
                else -> converter(source, raw, name, prop.returnType)
            }
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

    fun toDoc(req: Request, name:String): ContentFile? {
        // Conversions.toDoc(data.getString(paramName))
        val doc = when(req){
            is RequestSupport -> req.getDoc(name)
            else -> null
        }
        return doc
    }


    private fun getNullOrDefault(cls: KClass<*>, prop: KProperty<*>): Any? {
        val instance = defaults.get(cls.qualifiedName)

        // Case 1: No sample model to retrieve defaults for
        if(instance == null) return null

        // Case 2: Sample model exists, check property
        val defaultValue = Reflector.getFieldValue(instance, prop)
        return defaultValue
    }
}
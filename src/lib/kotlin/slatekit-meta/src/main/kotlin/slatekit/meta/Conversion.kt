package slatekit.meta

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import slatekit.common.Conversions
import slatekit.common.requests.Request
import slatekit.common.requests.RequestSupport
import slatekit.common.smartvalues.SmartCreation
import slatekit.common.smartvalues.SmartValue
import slatekit.common.types.Doc
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.companionObjectInstance

class Conversion(val req: Request, val converter: (parent: Any, raw: Any?, paramType: KType) -> Any?) {

    fun toList(array: JSONArray, tpe: KType): List<*> {
        val items = array.map { item ->
            item?.let { jsonItem -> converter(array, jsonItem, tpe) }
        }.filterNotNull()
        return items
    }

    fun toMap(obj: JSONObject, tpeKey: KType, tpeVal: KType): Map<*, *> {
        val keyConverter = Conversions.converterFor(tpeKey.javaClass)
        val items = obj.map { entry ->
            val key = keyConverter(entry.key?.toString()!!)
            val keyVal = converter(obj, entry.value, tpeVal)
            Pair(key, keyVal)
        }.filterNotNull().toMap()
        return items
    }

    fun toObject(obj: JSONObject, tpe: KType): Any {
        val cls = tpe.classifier as KClass<*>
        val props = Reflector.getProperties(cls)
        val items = props.map { prop ->
            val raw = obj.get(prop.name)
            val converted = converter(obj, raw, prop.returnType)
            converted
        }
        val instance = Reflector.createWithArgs<Any>(cls, items.toTypedArray())
        return instance
    }

    fun toSmartValue(txt: String, tpe: KType): SmartValue {

        val cls = tpe.classifier as KClass<*>
        val creator = cls.companionObjectInstance as SmartCreation<*>
        val result = creator.of(txt)
        return result
    }

    fun toDoc(name:String): Doc? {
        // Conversions.toDoc(data.getString(paramName))
        val doc = when(req){
            is RequestSupport -> req.getDoc(name)
            else -> null
        }
        return doc
    }
}
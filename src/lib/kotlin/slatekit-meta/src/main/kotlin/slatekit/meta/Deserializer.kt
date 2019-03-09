/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.meta

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.common.*
import slatekit.common.encrypt.*
import slatekit.common.requests.InputArgs
import slatekit.common.requests.Request
import slatekit.common.smartvalues.SmartCreation
import slatekit.common.smartvalues.SmartValue
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.createType

/**
 * Deserializes data ( as Inputs ) into the parameter types
 * represented by rawParams
 */
open class Deserializer(
        private val req: Request,
        private val enc: Encryptor? = null,
        private val converters: (Map<String, (Request, JSONObject, KType) -> Any?>) = mapOf()
) {

    private val typeRequest = Request::class.createType()
    private val typeMeta = Metadata::class.createType()

    open fun deserialize(parameters: List<KParameter>): Array<Any?> {

        val data: Inputs = req.data
        val meta: Metadata = req.meta
        // Check each parameter to api call
        val inputs = mutableListOf<Any?>()
        val jsonRaw = data.raw as? JSONObject
        for (ndx in 0 until parameters.size) {
            // Get each parameter to the method
            val parameter = parameters[ndx]
            val paramName = parameter.name!!
            val paramType = parameter.type
            val result: Any? = when (paramType) {

                // Basic types
                KTypes.KStringType -> Conversions.handleString(data.getString(paramName))
                KTypes.KBoolType -> data.getBool(paramName)
                KTypes.KShortType -> data.getShort(paramName)
                KTypes.KIntType -> data.getInt(paramName)
                KTypes.KLongType -> data.getLong(paramName)
                KTypes.KFloatType -> data.getFloat(paramName)
                KTypes.KDoubleType -> data.getDouble(paramName)
                KTypes.KLocalDateType -> data.getLocalDate(paramName)
                KTypes.KLocalTimeType -> data.getLocalTime(paramName)
                KTypes.KLocalDateTimeType -> data.getLocalDateTime(paramName)
                KTypes.KZonedDateTimeType -> data.getZonedDateTime(paramName)
                KTypes.KDateTimeType -> data.getDateTime(paramName)
                KTypes.KUUIDType -> UUID.fromString(data.getString(paramName))

                // Raw request
                typeRequest -> req

                // Raw meta
                typeMeta -> meta

                // Doc/File reference ( only if allowed )
                KTypes.KDocType -> Conversions.toDoc(data.getString(paramName))

                // Map from string string delimited pairs
                KTypes.KVarsType -> Conversions.toVars(data.getString(paramName))

                // Decryption from encrypted types
                KTypes.KDecIntType -> enc?.let { e -> EncInt(data.getString(paramName), e.decrypt(data.getString(paramName)).toInt()) } ?: EncInt("", 0)
                KTypes.KDecLongType -> enc?.let { e -> EncLong(data.getString(paramName), e.decrypt(data.getString(paramName)).toLong()) } ?: EncLong("", 0L)
                KTypes.KDecDoubleType -> enc?.let { e -> EncDouble(data.getString(paramName), e.decrypt(data.getString(paramName)).toDouble()) } ?: EncDouble("", 0.0)
                KTypes.KDecStringType -> enc?.let { e -> EncString(data.getString(paramName), e.decrypt(data.getString(paramName))) } ?: EncString("", "")

                // Complex type
                else -> handleComplex(data, parameter, paramType, jsonRaw, data.getString(paramName))
            }
            inputs.add(result)
        }

        return inputs.toTypedArray()
    }

    open fun convert(parameters: List<KParameter>, text: String): Array<Any?> {
        val jsonObj = JSONParser().parse(text) as JSONObject
        return convert(parameters, jsonObj)
    }

    /**
     * converts the JSON object data into the instances of the parameter types
     * @param parameters: The parameter info to convert
     * @param jsonObj : The json object to containing the data
     */
    open fun convert(parameters: List<KParameter>, jsonObj: JSONObject): Array<Any?> {

        // Check each parameter to api call
        val inputs = (0 until parameters.size).map { index ->
            val parameter = parameters[index]
            convert(parameter, jsonObj)
        }
        return inputs.toTypedArray()
    }

    /**
     * converts data from the json object as an instance of the parameter type
     */
    fun convert(parameter: KParameter, jsonObj: JSONObject): Any? {
        val paramName = parameter.name!!
        val paramType = parameter.type
        val data = jsonObj.get(paramName)
        val result = convert(jsonObj, data, paramType)
        return result
    }

    /**
     * converts
     */
    fun convert(parent: Any, raw: Any?, paramType: KType): Any? {
        return when (paramType.classifier) {
        // Basic types
            KTypes.KStringType.classifier -> raw?.let { Conversions.handleString(it) }
            KTypes.KBoolType.classifier -> raw?.toString()?.toBoolean()
            KTypes.KShortType.classifier -> raw?.toString()?.toShort()
            KTypes.KIntType.classifier -> raw?.toString()?.toInt()
            KTypes.KLongType.classifier -> raw?.toString()?.toLong()
            KTypes.KFloatType.classifier -> raw?.toString()?.toFloat()
            KTypes.KDoubleType.classifier -> raw?.toString()?.toDouble()
            KTypes.KLocalDateType.classifier -> raw?.let { Conversions.toLocalDate(it as String) }
            KTypes.KLocalTimeType.classifier -> raw?.let { Conversions.toLocalTime(it as String) }
            KTypes.KLocalDateTimeType.classifier -> raw?.let { Conversions.toLocalDateTime(it as String) }
            KTypes.KZonedDateTimeType.classifier -> raw?.let { Conversions.toZonedDateTime(it as String) }
            KTypes.KDateTimeType.classifier -> raw?.let { Conversions.toDateTime(it as String) }
            KTypes.KDecIntType.classifier -> enc?.let { e -> EncInt(raw as String, e.decrypt(raw).toInt()) } ?: EncInt("", 0)
            KTypes.KDecLongType.classifier -> enc?.let { e -> EncLong(raw as String, e.decrypt(raw).toLong()) } ?: EncLong("", 0L)
            KTypes.KDecDoubleType.classifier -> enc?.let { e -> EncDouble(raw as String, e.decrypt(raw).toDouble()) } ?: EncDouble("", 0.0)
            KTypes.KDecStringType.classifier -> enc?.let { e -> EncString(raw as String, e.decrypt(raw)) } ?: EncString("", "")
            KTypes.KVarsType.classifier -> raw?.let { Conversions.toVars(it) }
            KTypes.KUUIDType.classifier -> UUID.fromString(raw.toString())

        // Complex type
            else -> handleComplex(parent, raw, paramType)
        }
    }

    /**
     * Handles building of a list from various source types
     * @return
     */
    fun handleComplex(data: Inputs, parameter: KParameter, tpe: KType, jsonRaw: JSONObject?, raw: Any?): Any? {
        val paramName = parameter.name!!
        val cls = tpe.classifier as KClass<*>

        val result = if (cls.supertypes.indexOf(KTypes.KSmartValueType) >= 0) {
            handleSmartString(raw, tpe)
        } else if (cls.supertypes.indexOf(KTypes.KSmartValuedType) >= 0) {
            handleSmartString(raw, tpe)
        } else if (cls.supertypes.indexOf(KTypes.KEnumLikeType) >= 0) {
            val enumVal = data.get(paramName)
            Reflector.getEnumValue(cls, enumVal)
        } else if (jsonRaw == null) {
            // Case 1: List<*>
            if (cls == List::class) {
                val listType = tpe.arguments[0]!!.type!!
                val listCls = KTypes.getClassFromType(listType)
                data.getList(paramName, listCls.java)
            }
            // Case 2: Map<*,*>
            else if (cls == Map::class) {
                val tpeKey = tpe.arguments[0].type!!
                val tpeVal = tpe.arguments[1].type!!
                val clsKey = KTypes.getClassFromType(tpeKey)
                val clsVal = KTypes.getClassFromType(tpeVal)
                val emptyMap = mapOf<Any, Any>()
                data.getMap(paramName, clsKey.java, clsVal.java)
            }
            // Case 3: Smart String ( e.g. PhoneUS, Email, SSN, ZipCode )
            // Refer to slatekit.common.types
            else if (cls.supertypes.indexOf(KTypes.KSmartValueType) >= 0) {
                handleSmartString(raw, tpe)
            }
            // Case 4: Object / Complex type
            else {
                val json = if (jsonRaw == null) {
                    val obj = JSONObject()
                    if (data is InputArgs) {
                        val map = data._map
                        map.entries.forEach { pair ->
                            obj.put(pair.key, pair.value)
                        }
                    }
                    obj
                } else jsonRaw
                convert(parameter, json)
            }
        } else {
            convert(parameter, jsonRaw!!)
        }
        return result
    }

    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleComplex(parent: Any, raw: Any?, tpe: KType): Any? {
        val cls = tpe.classifier as KClass<*>
        val fullName = cls.qualifiedName
        return if (cls == List::class) {
            handleList(raw, tpe)
        } else if (cls == Map::class) {
            handleMap(raw, tpe)
        } else if (converters.containsKey(fullName)) {
            converters[fullName]?.invoke(req, parent as JSONObject, tpe)
        }
        // Case 3: Smart String ( e.g. PhoneUS, Email, SSN, ZipCode )
        // Refer to slatekit.common.types
        else if (cls.supertypes.indexOf(KTypes.KSmartValueType) >= 0) {
            handleSmartString(raw, tpe)
        }
        // Case 4: Slate Kit Enm
        else if (cls.supertypes.indexOf(KTypes.KEnumLikeType) >= 0) {
            Reflector.getEnumValue(cls, raw)
        } else {
            handleObject(raw, tpe)!!
        }
    }

    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleList(raw: Any?, tpe: KType): List<*> {
        val listType = tpe.arguments[0]!!.type!!
        val items = when (raw) {
            is JSONArray -> parseList(raw, listType)
            null -> listOf<Any>()
            "null" -> listOf<Any>()
            "\"\"" -> listOf<Any>()
            else -> listOf<Any>()
        }
        return items
    }

    /**
     * Handle building of a map from various sources
     * @param parameter
     * @param args
     * @param paramName
     * @return
     */
    fun handleMap(raw: Any?, tpe: KType): Map<*, *>? {
        val tpeKey = tpe.arguments[0].type!!
        val tpeVal = tpe.arguments[1].type!!
        val clsKey = KTypes.getClassFromType(tpeKey)
        val clsVal = KTypes.getClassFromType(tpeVal)
        val emptyMap = mapOf<Any, Any>()
        val items = when (raw) {
            is JSONObject -> parseMap(raw, tpeKey, tpeVal)
            null -> emptyMap
            "null" -> emptyMap
            "\"\"" -> emptyMap
            else -> emptyMap
        }
        return items
    }

    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleSmartString(raw: Any?, paramType: KType): Any? {
        return when (raw) {
            null -> null
            "null" -> null
            else -> parseSmartString(raw?.toString() ?: "", paramType)
        }
    }

    /**
     * Handle building of an object from various sources.
     * @param parameter
     * @param args
     * @param paramName
     * @return
     */
    fun handleObject(raw: Any?, paramType: KType): Any? {
        return when (raw) {
            is JSONObject -> parseObject(raw, paramType)
            null -> null
            "null" -> null
            "\"\"" -> null
            "" -> null
            else -> null
        }
    }

    fun parseList(array: JSONArray, tpe: KType): List<*> {
        val items = array.map { item ->
            item?.let { jsonItem -> convert(array, jsonItem, tpe) }
        }.filterNotNull()
        return items
    }

    fun parseMap(obj: JSONObject, tpeKey: KType, tpeVal: KType): Map<*, *> {
        val keyConverter = Conversions.converterFor(tpeKey.javaClass)
        val items = obj.map { entry ->
            val key = keyConverter(entry.key?.toString()!!)
            val keyVal = convert(obj, entry.value, tpeVal)
            Pair(key, keyVal)
        }.filterNotNull().toMap()
        return items
    }

    fun parseObject(obj: JSONObject, tpe: KType): Any {
        val cls = tpe.classifier as KClass<*>
        val props = Reflector.getProperties(cls)
        val items = props.map { prop ->
            val raw = obj.get(prop.name)
            val converted = convert(obj, raw, prop.returnType)
            converted
        }
        val instance = Reflector.createWithArgs<Any>(cls, items.toTypedArray())
        return instance
    }

    fun parseSmartString(txt: String, tpe: KType): SmartValue {

        val cls = tpe.classifier as KClass<*>
        val creator = cls.companionObjectInstance as SmartCreation<*>
        val result = creator.of(txt)
        return result
    }
}

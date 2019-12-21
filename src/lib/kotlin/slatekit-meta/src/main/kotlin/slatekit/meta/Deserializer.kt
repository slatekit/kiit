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
import slatekit.results.Err
import slatekit.results.ExceptionErr
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * De-serializes data ( as Inputs ) into the parameter types
 * represented by rawParams
 */
open class Deserializer(
        private val req: Request,
        private val enc: Encryptor? = null,
        private val decoders: Map<String, Decoder> = mapOf()
) {

    private val conversion = Conversion(this::convert)
    private val typeRequest = Request::class.createType()
    private val typeMeta = Metadata::class.createType()


    /**
     * Deserializes the JSON text associated with the parameters supplied
     */
    open fun deserialize(parameters: List<KParameter>, text: String): Array<Any?> {
        val jsonObj = JSONParser().parse(text) as JSONObject
        return deserialize(parameters, jsonObj)
    }

    /**
     * converts the JSON object data into the instances of the parameter types
     * @param parameters: The parameter info to convert
     * @param jsonObj : The json object to containing the data
     */
    open fun deserialize(parameters: List<KParameter>, jsonObj: JSONObject): Array<Any?> {

        // Check each parameter to api call
        val inputs = (0 until parameters.size).map { index ->
            val parameter = parameters[index]
            deserialize(parameter, jsonObj)
        }
        return inputs.toTypedArray()
    }

    /**
     * converts data from the json object as an instance of the parameter type
     */
    open fun deserialize(parameter: KParameter, jsonObj: JSONObject): Any? {
        val paramName = parameter.name!!
        val paramType = parameter.type
        val data = jsonObj.get(paramName)
        val result = convert(jsonObj, data, paramType)
        return result
    }


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
            val result:Any? = try {
                when (paramType) {

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
                    KTypes.KDocType -> conversion.toDoc(req, paramName)

                    // Map from string string delimited pairs
                    KTypes.KVarsType -> Conversions.toVars(data.getString(paramName))

                    // Decryption from encrypted types
                    KTypes.KDecIntType -> enc?.let { e -> EncInt(data.getString(paramName), e.decrypt(data.getString(paramName)).toInt()) } ?: EncInt("", 0)
                    KTypes.KDecLongType -> enc?.let { e -> EncLong(data.getString(paramName), e.decrypt(data.getString(paramName)).toLong()) } ?: EncLong("", 0L)
                    KTypes.KDecDoubleType -> enc?.let { e -> EncDouble(data.getString(paramName), e.decrypt(data.getString(paramName)).toDouble()) } ?: EncDouble("", 0.0)
                    KTypes.KDecStringType -> enc?.let { e -> EncString(data.getString(paramName), e.decrypt(data.getString(paramName))) } ?: EncString("", "")

                    // Complex type
                    else -> {
                        handleComplex(data, parameter, paramType, jsonRaw, data.getString(paramName))
                    }
                }
            }
            catch(ex:Exception) {
                val errValue = data.getStringOrNull(paramName)
                val errField = Err.on(paramName, errValue ?: "", "Invalid value", ex)
                val errList = Err.ErrorList(listOf(errField), ex.message ?: "Invalid value")
                throw ExceptionErr("Error while converting parameters", errList)
            }
            inputs.add(result)
        }

        return inputs.toTypedArray()
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
    private fun handleComplex(data: Inputs, parameter: KParameter, tpe: KType, jsonRaw: JSONObject?, raw: Any?): Any? {
        val paramName = parameter.name!!
        val cls = tpe.classifier as KClass<*>

        val result = if (cls.supertypes.indexOf(KTypes.KSmartValueType) >= 0) {
            handleSmartValue(raw, tpe)
        } else if (cls.supertypes.indexOf(KTypes.KSmartValuedType) >= 0) {
            handleSmartValue(raw, tpe)
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
                handleSmartValue(raw, tpe)
            }
            // Case 4: Object / Complex type
            else {
                val json = if (jsonRaw == null) {
                    val obj = JSONObject()
                    if (data is InputArgs) {
                        val map = data.map
                        map.entries.forEach { pair ->
                            obj.put(pair.key, pair.value)
                        }
                    }
                    obj
                } else jsonRaw
                deserialize(parameter, json)
            }
        } else {
            deserialize(parameter, jsonRaw!!)
        }
        return result
    }

    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    private fun handleComplex(parent: Any, raw: Any?, tpe: KType): Any? {
        val cls = tpe.classifier as KClass<*>
        val fullName = cls.qualifiedName
        return if (cls == List::class) {
            handleList(raw, tpe)
        } else if (cls == Map::class) {
            handleMap(raw, tpe)
        } else if (decoders.containsKey(fullName)) {
            val decoder = decoders[fullName]
            decoder?.decode(req, parent as JSONObject, tpe)
        }
        // Case 3: Smart String ( e.g. PhoneUS, Email, SSN, ZipCode )
        // Refer to slatekit.common.types
        else if (cls.supertypes.indexOf(KTypes.KSmartValueType) >= 0) {
            handleSmartValue(raw, tpe)
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
    private fun handleList(raw: Any?, tpe: KType): List<*> {
        val listType = tpe.arguments[0]!!.type!!
        return when (raw) {
            is JSONArray -> conversion.toList(raw, listType)
            else -> handle(raw, listOf<Any>()) { listOf<Any>() } as List<*>
        }
    }

    /**
     * Handle building of a map from various sources
     */
    private fun handleMap(raw: Any?, tpe: KType): Map<*, *>? {
        val tpeKey = tpe.arguments[0].type!!
        val tpeVal = tpe.arguments[1].type!!
        val emptyMap = mapOf<Any, Any>()
        val items = when (raw) {
            is JSONObject -> conversion.toMap(raw, tpeKey, tpeVal)
            else -> handle(raw, emptyMap) { emptyMap } as Map<*, *>
        }
        return items
    }

    private fun handleSmartValue(raw: Any?, paramType: KType): Any? {
        return handle(raw, null) { conversion.toSmartValue(raw?.toString() ?: "", paramType) }
    }

    private fun handleObject(raw: Any?, paramType: KType): Any? {
        return when (raw) {
            is JSONObject -> conversion.toObject(raw, paramType)
            else -> handle(raw, null) { null }
        }
    }

    private fun handle(raw:Any?, nullValue:Any?, elseValue:() -> Any?):Any? {
        return when (raw) {
            null   -> nullValue
            "null" -> nullValue
            ""     -> nullValue
            "\"\"" -> nullValue
            else   -> elseValue()
        }
    }
}

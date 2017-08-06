/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.meta

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.common.Request
import slatekit.common.SmartString
import slatekit.common.Conversions
import slatekit.common.encrypt.*
import slatekit.common.types.Email
import slatekit.common.types.PhoneUS
import slatekit.common.types.SSN
import slatekit.common.types.ZipCode
import kotlin.reflect.*
import kotlin.reflect.full.createType


/**
 * Json converter
 */
class Converter(val enc: Encryptor? = null,
                val converters:Map<String,(JSONObject, KType) -> Any> = mapOf(),
                val smartStrings:Map<String, SmartString> = mapOf()) {

    // This could be a reference to file doc e.g. user://myapp/conf/apikey.conf
    val TypeRequest = Request::class.createType()


    fun convert(parameters:List<KParameter>, text:String): Array<Any?> {
        val jsonObj = JSONParser().parse(text) as JSONObject
        return convert(parameters, jsonObj)
    }


    /**
     * converts the JSON object data into the instances of the parameter types
     * @param parameters: The parameter info to convert
     * @param jsonObj   : The json object to containing the data
     */
    fun convert(parameters:List<KParameter>, jsonObj: JSONObject): Array<Any?> {

        // Check each parameter to api call
        val params = parameters
        val inputs = (0..params.size - 1).map{ index ->
            val parameter = params[index]
            convert(parameter, jsonObj)
        }
        return inputs.toTypedArray()
    }


    /**
     * Gets data from the json object as an instance of the parameter type
     */
    fun convert(parameter: KParameter, jsonObj: JSONObject): Any? {
        val paramName = parameter.name!!
        val paramType = parameter.type
        val data = jsonObj.get(paramName)
        val result = convert(jsonObj, data, paramType)
        return result
    }


    fun convert(parent:Any, raw:Any?, paramType: KType): Any? {
        return when (paramType) {
            // Basic types
            KTypes.KStringType        -> Conversions.handleString(raw)
            KTypes.KBoolType          -> raw.toString().toBoolean()
            KTypes.KShortType         -> raw.toString().toShort()
            KTypes.KIntType           -> raw.toString().toInt()
            KTypes.KLongType          -> raw.toString().toLong()
            KTypes.KFloatType         -> raw.toString().toFloat()
            KTypes.KDoubleType        -> raw.toString().toDouble()
            KTypes.KLocalDateType     -> Conversions.toLocalDate(raw as String)
            KTypes.KLocalTimeType     -> Conversions.toLocalTime(raw as String)
            KTypes.KLocalDateTimeType -> Conversions.toLocalDateTime(raw as String)
            KTypes.KZonedDateTimeType -> Conversions.toZonedDateTime(raw as String)
            KTypes.KDateTimeType      -> Conversions.toDateTime(raw as String)
            KTypes.KDecIntType        -> enc?.let { e -> DecInt(e.decrypt(raw as String).toInt()) } ?: DecInt(0)
            KTypes.KDecLongType       -> enc?.let { e -> DecLong(e.decrypt(raw as String).toLong()) } ?: DecLong(0L)
            KTypes.KDecDoubleType     -> enc?.let { e -> DecDouble(e.decrypt(raw as String).toDouble()) } ?: DecDouble(0.0)
            KTypes.KDecStringType     -> enc?.let { e -> DecString(e.decrypt(raw as String)) } ?: DecString("")
            KTypes.KVarsType          -> Conversions.toVars(raw)

            // Complex type
            else                    -> handleComplex(parent, raw, paramType)
        }
    }


    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleComplex(parent:Any, raw:Any?, tpe: KType): Any? {
        val cls = tpe.classifier as KClass<*>
        val fullName = cls.qualifiedName
        return if(cls == List::class){
            handleList(raw, tpe)
        }
        else if(cls == Map::class){
            handleMap(raw, tpe)
        }
        else if(converters.containsKey(fullName)){
            converters[fullName]?.invoke(parent as JSONObject, tpe)!!
        }
        // Case 3: Smart String ( e.g. PhoneUS, Email, SSN, ZipCode )
        // Refer to slatekit.common.types
        else if ( cls.supertypes.indexOf(KTypes.KSmartStringType) >= 0 ) {
            handleSmartString(raw, tpe)
        }
        else {
            handleObject(raw, tpe)!!
        }
    }


    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleList(raw:Any?, tpe: KType): List<*> {
        val listType = tpe.arguments[0]!!.type!!
        val items = when (raw) {
            is JSONArray -> parseList(raw, listType)
            null         -> listOf<Any>()
            "null"       -> listOf<Any>()
            "\"\""       -> listOf<Any>()
            else         -> listOf<Any>()
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
    fun handleMap(raw:Any?, tpe: KType): Map<*, *>? {
        val tpeKey = tpe.arguments[0].type!!
        val tpeVal = tpe.arguments[1].type!!
        val clsKey = KTypes.getClassFromType(tpeKey)
        val clsVal = KTypes.getClassFromType(tpeVal)
        val emptyMap = mapOf<Any, Any>()
        val items = when (raw) {
            is JSONObject -> parseMap(raw, tpeKey, tpeVal)
            null          -> emptyMap
            "null"        -> emptyMap
            "\"\""        -> emptyMap
            else          -> emptyMap
        }
        return items
    }


    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleSmartString(raw:Any?, paramType: KType): Any? {
        return when (raw) {
            null   -> null
            "null" -> null
            else   -> parseSmartString(raw?.toString() ?: "", paramType)
        }
    }


    /**
     * Handle building of an object from various sources.
     * @param parameter
     * @param args
     * @param paramName
     * @return
     */
    fun handleObject(raw:Any?, paramType: KType): Any? {
        return when (raw) {
            is JSONObject -> parseObject(raw, paramType)
            null          -> null
            "null"        -> null
            "\"\""        -> null
            ""            -> null
            else          -> null
        }
    }


    fun parseList(array: JSONArray, tpe: KType):List<*> {
        val items = array.map { item ->
            item?.let { jsonItem -> convert(array, jsonItem, tpe) }
        }.filterNotNull()
        return items
    }


    fun parseMap(obj: JSONObject, tpeKey: KType, tpeVal: KType):Map<*,*> {
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


    fun parseSmartString(txt:String, tpe: KType) : SmartString {

        val cls = tpe.classifier as KClass<*>
        val smartString = when ( cls ) {
            PhoneUS::class -> PhoneUS(txt)
            Email::class   -> Email(txt)
            ZipCode::class -> ZipCode(txt)
            SSN::class     -> SSN(txt)
            else           -> Reflector.createWithArgs<Any>(cls, arrayOf(txt)) as SmartString
        }
        return smartString
    }
}
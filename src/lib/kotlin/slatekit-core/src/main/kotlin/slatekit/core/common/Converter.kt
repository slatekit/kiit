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

package slatekit.core.common

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.common.*
import slatekit.common.encrypt.*
import kotlin.reflect.*
import kotlin.reflect.full.createType


/**
 * Json converter
 */
class Converter(val enc:Encryptor? = null,
                val converters:Map<String,(JSONObject, KType) -> Any> = mapOf()) {

    // This could be a reference to file doc e.g. user://myapp/conf/apikey.conf
    val TypeDoc = Doc::class.createType()
    val TypeVars = Vars::class.createType()
    val TypeRequest = Request::class.createType()


    fun convert(parameters:List<KParameter>, text:String): Array<Any> {
        val jsonObj = JSONParser().parse(text) as JSONObject
        return convert(parameters, jsonObj)
    }


    /**
     * converts the JSON object data into the instances of the parameter types
     * @param parameters: The parameter info to convert
     * @param jsonObj   : The json object to containing the data
     */
    fun convert(parameters:List<KParameter>, jsonObj:JSONObject): Array<Any> {

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
    fun convert(parameter:KParameter, jsonObj: JSONObject): Any {

        val paramName = parameter.name!!
        val paramType = parameter.type
        val data = jsonObj.get(paramName)
        val result = convert(jsonObj, data, paramType)
        return result
    }


    fun convert(parent:Any, raw:Any?, paramType: KType): Any {
        return when (paramType) {
            // Basic types
            Types.StringType        -> handleString(raw)
            Types.BoolType          -> raw as Boolean
            Types.ShortType         -> raw.toString().toShort()
            Types.IntType           -> raw.toString().toInt()
            Types.LongType          -> raw.toString().toLong()
            Types.FloatType         -> raw.toString().toFloat()
            Types.DoubleType        -> raw as Double
            Types.LocalDateType     -> Conversions.toLocalDate(raw as String)
            Types.LocalTimeType     -> Conversions.toLocalTime(raw as String)
            Types.LocalDateTimeType -> Conversions.toLocalDateTime(raw as String)
            Types.ZonedDateTimeType -> Conversions.toZonedDateTime(raw as String)
            Types.DateTimeType      -> Conversions.toDateTime(raw as String)
            Types.TypeDecInt        -> enc?.let { e -> DecInt(e.decrypt(raw as String).toInt()) } ?: DecInt(0)
            Types.TypeDecLong       -> enc?.let { e -> DecLong(e.decrypt(raw as String).toLong()) } ?: DecLong(0L)
            Types.TypeDecDouble     -> enc?.let { e -> DecDouble(e.decrypt(raw as String).toDouble()) } ?: DecDouble(0.0)
            Types.TypeDecString     -> enc?.let { e -> DecString(e.decrypt(raw as String)) } ?: DecString("")
            TypeVars                -> handleVars(raw)

            // Complex type
            else                    -> handleComplex(parent, raw, paramType)
        }
    }


    /**
     * Builds a string parameter ensuring that nulls are avoided.
     * @param args
     * @param paramName
     * @return
     */
    fun handleString(data:Any?): String {
        // As a design choice, this marshaller will only pass empty string to
        // API methods instead of null
        return when(data) {
            null      -> ""
            "null"    -> ""
            is String -> if(data.isNullOrEmpty()) "" else data
            else      -> data.toString()
        }
    }


    /**
     * Builds a Vars object which is essentially a lookup of items by both index and key
     * @param args
     * @param paramName
     * @return
     */
    fun handleVars(data:Any?): Vars {
        return when(data) {
            null      -> Vars.apply("")
            "null"    -> Vars.apply("")
            is String -> if(data.isNullOrEmpty()) Vars.apply("") else Vars.apply(data)
            else      -> Vars.apply("")
        }
    }


    /**
     * Builds a Doc object by reading the file content from the referenced uri
     * e.g.
     * 1. "user://slatekit/temp/file1.txt"    reference user directory
     * 2. "file://c:/slatekit/temp/file.txt"  reference file explicitly
     * @param args
     * @param paramName
     * @return
     */
    fun handleDoc(uri:String): Doc {
        val doc = Uris.readDoc(uri)
        return doc ?: Doc.text("", "")
    }


    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleComplex(parent:Any, raw:Any?, tpe:KType): Any {
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
    fun handleList(raw:Any?, tpe:KType): List<*> {
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
    fun handleMap(raw:Any?, tpe: KType): Map<*, *> {
        val tpeKey = tpe.arguments[0].type!!
        val tpeVal = tpe.arguments[1].type!!
        val clsKey = Types.getClassFromType(tpeKey)
        val clsVal = Types.getClassFromType(tpeVal)
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
     * Handle building of an object from various sources.
     * @param parameter
     * @param args
     * @param paramName
     * @return
     */
    fun handleObject(raw:Any?, paramType: KType): Any? {
        return when (raw) {
            is JSONObject -> parseObject(raw, paramType)
            null   -> null
            "null" -> null
            "\"\"" -> null
            ""     -> null
            else   -> null
        }
    }


    fun parseList(array:JSONArray, tpe:KType):List<*> {
        val items = array.map { item ->
            item?.let { jsonItem -> convert(array, jsonItem, tpe) }
        }.filterNotNull()
        return items
    }


    fun parseMap(obj:JSONObject, tpeKey:KType, tpeVal:KType):Map<*,*> {
        val keyConverter = Conversions.converterFor(tpeKey)
        val items = obj.map { entry ->
            val key = keyConverter(entry.key?.toString()!!)
            val keyVal = convert(obj, entry.value, tpeVal)
            Pair(key, keyVal)
        }.filterNotNull().toMap()
        return items
    }


    fun parseObject(obj:JSONObject, tpe:KType): Any {
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
}
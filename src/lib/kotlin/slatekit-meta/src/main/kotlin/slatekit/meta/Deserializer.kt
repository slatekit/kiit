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

import org.json.simple.JSONObject
import slatekit.common.*
import slatekit.common.encrypt.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType


/**
 * Deserializes data ( as Inputs ) into the parameter types
 * represented by rawParams
 */
open class Deserializer(
        private val converter:Converter,
        private val enc: Encryptor? = null) {

    val TypeRequest = Request::class.createType()
    val TypeMeta    = Meta::class.createType()


    open fun deserialize(parameters: List<KParameter>, data: Inputs, meta: Meta?, source:Any?): Array<Any?> {

        // Check each parameter to api call
        val inputs = mutableListOf<Any?>()
        val jsonRaw = data.raw as? JSONObject
        for (ndx in 0 until parameters.size) {
            // Get each parameter to the method
            val parameter = parameters[ndx]
            val paramName = parameter.name!!
            val paramType = parameter.type
            val result:Any?? = when (paramType) {

                // Basic types
                KTypes.KStringType        -> Conversions.handleString(data.getString(paramName))
                KTypes.KBoolType          -> data.getBool(paramName)
                KTypes.KShortType         -> data.getShort(paramName)
                KTypes.KIntType           -> data.getInt(paramName)
                KTypes.KLongType          -> data.getLong(paramName)
                KTypes.KFloatType         -> data.getFloat(paramName)
                KTypes.KDoubleType        -> data.getDouble(paramName)
                KTypes.KLocalDateType     -> data.getLocalDate(paramName)
                KTypes.KLocalTimeType     -> data.getLocalTime(paramName)
                KTypes.KLocalDateTimeType -> data.getLocalDateTime(paramName)
                KTypes.KZonedDateTimeType -> data.getZonedDateTime(paramName)
                KTypes.KDateTimeType      -> data.getDateTime(paramName)

                // Raw request
                TypeRequest             -> source

                // Raw request
                TypeMeta                -> meta

                // Doc/File reference ( only if allowed )
                KTypes.KDocType          -> Conversions.toDoc(data.getString(paramName))

                // Map from string string delimited pairs
                KTypes.KVarsType         -> Conversions.toVars(data.getString(paramName))

                // Decryption from encrypted types
                KTypes.KDecIntType        -> enc?.let { e -> EncInt(data.getString(paramName), e.decrypt(data.getString(paramName)).toInt()) } ?: EncInt("", 0)
                KTypes.KDecLongType       -> enc?.let { e -> EncLong(data.getString(paramName), e.decrypt(data.getString(paramName)).toLong()) } ?: EncLong("", 0L)
                KTypes.KDecDoubleType     -> enc?.let { e -> EncDouble(data.getString(paramName), e.decrypt(data.getString(paramName)).toDouble()) } ?: EncDouble("", 0.0)
                KTypes.KDecStringType     -> enc?.let { e -> EncString(data.getString(paramName), e.decrypt(data.getString(paramName))) } ?: EncString("", "")

                // Complex type
                else                    -> handleComplex(data, parameter, paramType, jsonRaw, data.getString(paramName))
            }
            inputs.add(result)
        }

        return inputs.toTypedArray()
    }


    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleComplex(data: Inputs, parameter: KParameter, tpe: KType, jsonRaw: JSONObject?, raw:Any?): Any? {
        val paramName = parameter.name!!
        val cls = tpe.classifier as KClass<*>

        val result = if ( cls.supertypes.indexOf(KTypes.KSmartStringType) >= 0 ) {
            converter.handleSmartString(raw, tpe)
        }
        else if(jsonRaw == null){
            // Case 1: List<*>
            if(cls == List::class){
                val listType = tpe.arguments[0]!!.type!!
                val listCls = KTypes.getClassFromType(listType)
                data.getList(paramName, listCls.java)
            }
            // Case 2: Map<*,*>
            else if(cls == Map::class){
                val tpeKey = tpe.arguments[0].type!!
                val tpeVal = tpe.arguments[1].type!!
                val clsKey = KTypes.getClassFromType(tpeKey)
                val clsVal = KTypes.getClassFromType(tpeVal)
                val emptyMap = mapOf<Any, Any>()
                data.getMap(paramName,clsKey.java, clsVal.java)
            }
            // Case 3: Smart String ( e.g. PhoneUS, Email, SSN, ZipCode )
            // Refer to slatekit.common.types
            else if ( cls.supertypes.indexOf(KTypes.KSmartStringType) >= 0 ) {
                converter.handleSmartString(raw, tpe)
            }
            // Case 4: Object / Complex type
            else {
                val json = if(jsonRaw == null) {
                    val obj = JSONObject()
                    if(data is InputArgs){
                        val map = data._map
                        map.entries.forEach { pair ->
                            obj.put(pair.key, pair.value)
                        }
                    }
                    obj
                }
                else jsonRaw
                converter.convert(parameter, json)
            }
        } else {
            converter.convert(parameter, jsonRaw!!)
        }
        return result
    }
}

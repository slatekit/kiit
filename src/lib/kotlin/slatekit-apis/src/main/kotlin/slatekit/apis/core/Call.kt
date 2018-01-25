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

package slatekit.apis.core

import org.json.simple.JSONObject
import slatekit.apis.ApiConstants
import slatekit.apis.ApiRegAction
import slatekit.common.*
import slatekit.common.encrypt.*
import slatekit.meta.Converter
import slatekit.meta.KTypes
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Created by kreddy on 3/15/2016.
 */
class Call {

    val TypeRequest = Request::class.createType()


    fun fillArgsExact(callReflect: ApiRegAction, cmd: Request, allowLocalIO: Boolean = false,
                      enc: Encryptor? = null): Array<Any?> {
        return fillArgsForMethod(callReflect.member, cmd, cmd.data!!, allowLocalIO, enc)
    }


    fun fillArgsForMethod(call: KCallable<*>, cmd: Request, args: Inputs, allowLocalIO: Boolean = false, enc: Encryptor? = null): Array<Any?> {

        // Check each parameter to api call
        val inputs = mutableListOf<Any?>()
        val parameters = if (call.parameters.size == 1) listOf<KParameter>() else call.parameters.subList(1, call.parameters.size)
        val converter = Converter(enc)
        val jsonRaw = cmd.data?.raw as? JSONObject
        for (ndx in 0..parameters.size - 1) {
            // Get each parameter to the method
            val parameter = parameters[ndx]
            val paramName = parameter.name!!
            val paramType = parameter.type
            val result = when (paramType) {

                // Basic types
                KTypes.KStringType        -> Conversions.handleString(args.getString(paramName))
                KTypes.KBoolType          -> args.getBool(paramName)
                KTypes.KShortType         -> args.getShort(paramName)
                KTypes.KIntType           -> args.getInt(paramName)
                KTypes.KLongType          -> args.getLong(paramName)
                KTypes.KFloatType         -> args.getFloat(paramName)
                KTypes.KDoubleType        -> args.getDouble(paramName)
                KTypes.KLocalDateType     -> args.getLocalDate(paramName)
                KTypes.KLocalTimeType     -> args.getLocalTime(paramName)
                KTypes.KLocalDateTimeType -> args.getLocalDateTime(paramName)
                KTypes.KZonedDateTimeType -> args.getZonedDateTime(paramName)
                KTypes.KDateTimeType      -> args.getDateTime(paramName)

                // Raw request
                TypeRequest             -> cmd

                // Doc/File reference ( only if allowed )
                KTypes.KDocType          -> Conversions.toDoc(args.getString(paramName))

                // Map from string string delimited pairs
                KTypes.KVarsType         -> Conversions.toVars(args.getString(paramName))

                // Decryption from encrypted types
                KTypes.KDecIntType        -> enc?.let { e -> DecInt(e.decrypt(args.getString(paramName)).toInt()) } ?: DecInt(0)
                KTypes.KDecLongType       -> enc?.let { e -> DecLong(e.decrypt(args.getString(paramName)).toLong()) } ?: DecLong(0L)
                KTypes.KDecDoubleType     -> enc?.let { e -> DecDouble(e.decrypt(args.getString(paramName)).toDouble()) } ?: DecDouble(0.0)
                KTypes.KDecStringType     -> enc?.let { e -> DecString(e.decrypt(args.getString(paramName))) } ?: DecString("")

                // Complex type
                else                    -> handleComplex(converter, cmd, parameter, paramType, jsonRaw, args.getString(paramName))
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
    fun handleComplex(converter: Converter, req:Request, parameter:KParameter, tpe:KType, jsonRaw:JSONObject?, raw:Any?): Any? {
        val paramName = parameter.name!!
        return if(req.source == ApiConstants.SourceCLI){
            val cls = tpe.classifier as KClass<*>

            // Case 1: List<*>
            if(cls == List::class){
                val listType = tpe.arguments[0]!!.type!!
                val listCls = KTypes.getClassFromType(listType)
                req.data?.getList(paramName, listCls.java) ?: listOf<Any>()
            }
            // Case 2: Map<*,*>
            else if(cls == Map::class){
                val tpeKey = tpe.arguments[0].type!!
                val tpeVal = tpe.arguments[1].type!!
                val clsKey = KTypes.getClassFromType(tpeKey)
                val clsVal = KTypes.getClassFromType(tpeVal)
                val emptyMap = mapOf<Any, Any>()
                req.data?.getMap(paramName,clsKey.java, clsVal.java) ?: emptyMap
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
                    if(req.data is InputArgs){
                        val map = (req.data as InputArgs)._map
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
    }
}

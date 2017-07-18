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
import slatekit.common.*
import slatekit.common.encrypt.*
import slatekit.core.common.Converter
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
    val TypeDoc = Doc::class.createType()
    val TypeVars = Vars::class.createType()


    // Improve: Check this article:
    // http://www.cakesolutions.net/teamblogs/ways-to-pattern-match-generic-types-in-scala
    fun fillArgsExact(callReflect: Action, cmd: Request, allowLocalIO: Boolean = false,
                      enc: Encryptor? = null): Array<Any?> {
        return fillArgsForMethod(callReflect.member, cmd, cmd.args!!, allowLocalIO, enc)
    }


    fun fillArgsForMethod(call: KCallable<*>, cmd: Request, args: Inputs, allowLocalIO: Boolean = false, enc: Encryptor? = null): Array<Any?> {

        // Check each parameter to api call
        val inputs = mutableListOf<Any?>()
        val parameters = if (call.parameters.size == 1) listOf<KParameter>() else call.parameters.subList(1, call.parameters.size)
        val converter = Converter(enc)
        val jsonRaw = cmd.args?.raw as? JSONObject
        for (ndx in 0..parameters.size - 1) {
            // Get each parameter to the method
            val parameter = parameters[ndx]
            val paramName = parameter.name!!
            val paramType = parameter.type
            val result = when (paramType) {

                // Basic types
                Types.StringType        -> Conversions.handleString(args.getString(paramName))
                Types.BoolType          -> args.getBool(paramName)
                Types.ShortType         -> args.getShort(paramName)
                Types.IntType           -> args.getInt(paramName)
                Types.LongType          -> args.getLong(paramName)
                Types.FloatType         -> args.getFloat(paramName)
                Types.DoubleType        -> args.getDouble(paramName)
                Types.LocalDateType     -> args.getLocalDate(paramName)
                Types.LocalTimeType     -> args.getLocalTime(paramName)
                Types.LocalDateTimeType -> args.getLocalDateTime(paramName)
                Types.ZonedDateTimeType -> args.getZonedDateTime(paramName)
                Types.DateTimeType      -> args.getDateTime(paramName)

                // Raw request
                TypeRequest             -> cmd

                // Doc/File reference ( only if allowed )
                TypeDoc                 -> Conversions.toDoc(args.getString(paramName))

                // Map from string string delimited pairs
                TypeVars                -> Conversions.toVars(args.getString(paramName))

                // Decryption from encrypted types
                Types.TypeDecInt        -> enc?.let { e -> DecInt(e.decrypt(args.getString(paramName)).toInt()) } ?: DecInt(0)
                Types.TypeDecLong       -> enc?.let { e -> DecLong(e.decrypt(args.getString(paramName)).toLong()) } ?: DecLong(0L)
                Types.TypeDecDouble     -> enc?.let { e -> DecDouble(e.decrypt(args.getString(paramName)).toDouble()) } ?: DecDouble(0.0)
                Types.TypeDecString     -> enc?.let { e -> DecString(e.decrypt(args.getString(paramName))) } ?: DecString("")

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
    fun handleComplex(converter:Converter, req:Request, parameter:KParameter, tpe:KType, jsonRaw:JSONObject?, raw:Any?): Any? {
        val paramName = parameter.name!!
        return if(req.protocol == ApiConstants.ProtocolCLI){
            val cls = tpe.classifier as KClass<*>

            // Case 1: List<*>
            if(cls == List::class){
                val listType = tpe.arguments[0]!!.type!!
                val listCls = Types.getClassFromType(listType)
                req.args?.getList(paramName, listCls) ?: listOf<Any>()
            }
            // Case 2: Map<*,*>
            else if(cls == Map::class){
                val tpeKey = tpe.arguments[0].type!!
                val tpeVal = tpe.arguments[1].type!!
                val clsKey = Types.getClassFromType(tpeKey)
                val clsVal = Types.getClassFromType(tpeVal)
                val emptyMap = mapOf<Any, Any>()
                req.args?.getMap(paramName,clsKey, clsVal) ?: emptyMap
            }
            // Case 3: Smart String ( e.g. PhoneUS, Email, SSN, ZipCode )
            // Refer to slatekit.common.types
            else if ( cls.supertypes.indexOf(Types.SmartStringType) >= 0 ) {
                converter.handleSmartString(raw, tpe)
            }
            // Case 4: Object / Complex type
            else {
                converter.convert(parameter, jsonRaw!!)
            }
        } else {
            converter.convert(parameter, jsonRaw!!)
        }
    }
}

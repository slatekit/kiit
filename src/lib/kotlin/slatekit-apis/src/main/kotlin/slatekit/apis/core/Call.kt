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

import slatekit.common.*
import slatekit.common.encrypt.*
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType

/**
 * Created by kreddy on 3/15/2016.
 */
class Call {

    val TypeDecString = DecString::class.createType()
    val TypeDecInt = DecInt::class.createType()
    val TypeDecLong = DecLong::class.createType()
    val TypeDecDouble = DecDouble::class.createType()
    val TypeDoc = Doc::class.createType()
    val TypeVars = Vars::class.createType()
    val TypeRequest = Request::class.createType()


    /**
     * Builds a string parameter ensuring that nulls are avoided.
     * @param args
     * @param paramName
     * @return
     */
    fun handleStringParam(args: Inputs, paramName: String): String {
        val text = args.getString(paramName)
        val isNull = "null" == text

        // As a design choice, this marshaller will only pass empty string to
        // API methods instead of null
        return if (isNull || text.isNullOrEmpty())
            ""
        else
            text
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
    fun handleDocParam(args: Inputs, paramName: String): Doc {
        val uri = args.getString(paramName)
        val doc = Uris.readDoc(uri)
        return doc ?: Doc("", "", "", 0)
    }


    /**
     * Builds a Vars object which is essentially a lookup of items by both index and key
     * TODO: Add support for construction for various types, e..g string, list, map.
     * @param args
     * @param paramName
     * @return
     */
    fun handleVarsParam(args: Inputs, paramName: String): Vars {
        val text = args.getString(paramName)
        val isNull = "null" == text

        // As a design choice, this marshaller will only pass empty string to
        // API methods instead of null
        return if (isNull || text.isNullOrEmpty())
            Vars.apply("")
        else
            Vars.apply(text)
    }


    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    fun handleList(args: Inputs, paramName: String, parameter: KParameter): List<*> {
        val tpe = parameter.type.arguments[0]!!
        val cls = Types.getClassFromType(tpe.type!!)
        val items = args.getList(paramName, cls)
        return items
    }


    /**
     * Handle building of a map from various sources
     * @param parameter
     * @param args
     * @param paramName
     * @return
     */
    fun handleMap(args: Inputs, paramName: String, parameter: KParameter): Map<*, *> {
        val tpeKey = parameter.type.arguments[0].type!!
        val tpeVal = parameter.type.arguments[1].type!!
        val clsKey = Types.getClassFromType(tpeKey)
        val clsVal = Types.getClassFromType(tpeVal)
        val items = args.getMap(paramName, clsKey, clsVal)
        return items
    }


    /**
     * Handle building of an object from various sources.
     * @param parameter
     * @param args
     * @param paramName
     * @return
     */
    fun handleObject(args: Inputs, paramName: String, parameter: KParameter): Any {
        val complexObj = args.getObject(paramName)
        return complexObj?.let { c ->
            when (c) {
                "null" -> null
                "\"\"" -> null
                ""     -> null
                else   -> buildArgInstance(parameter, c as Inputs)
            }
        } ?: ""
    }


    /**
     * Builds a complex object
     * @param args
     * @param parameter
     * @param paramName
     * @return
     */
    fun handleComplex(args: Inputs, parameter: KParameter, paramName: String): Any {
        // Type names for lists/maps:
        // "kotlin.collections.List<kotlin.Int>"
        // "kotlin.collections.MutableList<kotlin.Int>"

        // TODO: Improve the type check for lists/maps
        val typeNameList = "kotlin.collections.List<"
        val typeNameMap = "kotlin.collections.Map<"
        val fullName = parameter.type.toString()

        return if (fullName.startsWith(typeNameList)) {
            handleList(args, paramName, parameter)
        }
        else if (fullName.startsWith(typeNameMap)) {
            handleMap(args, paramName, parameter)
        }
        else {
            handleObject(args, paramName, parameter)
        }
    }


    // Improve: Check this article:
    // http://www.cakesolutions.net/teamblogs/ways-to-pattern-match-generic-types-in-scala
    fun fillArgsExact(callReflect: Action, cmd: Request, allowLocalIO: Boolean = false,
                      enc: Encryptor? = null): Array<Any> {
        return fillArgsForMethod(callReflect.member, cmd, cmd.args!!, allowLocalIO, enc)
    }


    fun fillArgsForMethod(call: KCallable<*>, cmd: Request, args: Inputs, allowLocalIO: Boolean = false, enc: Encryptor? = null): Array<Any> {

        // Check each parameter to api call
        val inputs = mutableListOf<Any>()
        val parameters = if (call.parameters.size == 1) listOf<KParameter>() else call.parameters.subList(1, call.parameters.size)
        for (ndx in 0..parameters.size - 1) {
            // Get each parameter to the method
            val parameter = parameters[ndx]
            val paramName = parameter.name!!
            val paramType = parameter.type
            val result = when (paramType) {

            // Basic types
                Types.BoolType   -> args.getString(paramName).toBoolean()
                Types.ShortType  -> args.getString(paramName).toShort()
                Types.IntType    -> args.getString(paramName).toInt()
                Types.LongType   -> args.getString(paramName).toLong()
                Types.FloatType  -> args.getString(paramName).toFloat()
                Types.DoubleType -> args.getString(paramName).toDouble()
                Types.StringType -> handleStringParam(args, paramName)
                Types.DateType   -> DateTime.parseNumericVal(args.getString(paramName))

            // Raw request
                TypeRequest      -> cmd

            // Doc/File reference ( only if allowed )
                TypeDoc          -> handleDocParam(args, paramName)

            // Map from string string delimited pairs
                TypeVars         -> handleVarsParam(args, paramName)

            // Decryption from encrypted types
                TypeDecInt       -> enc?.let { e -> DecInt(e.decrypt(args.getString(paramName)).toInt()) } ?: DecInt(0)
                TypeDecLong      -> enc?.let { e -> DecLong(e.decrypt(args.getString(paramName)).toLong()) } ?: DecLong(0L)
                TypeDecDouble    -> enc?.let { e -> DecDouble(e.decrypt(args.getString(paramName)).toDouble()) } ?: DecDouble(0.0)
                TypeDecString    -> enc?.let { e -> DecString(e.decrypt(args.getString(paramName))) } ?: DecString("")

            // Complex type
                else             -> handleComplex(args, parameter, paramName)
            }

            inputs.add(result)
        }
        return inputs.toTypedArray()
    }


    private fun buildArgInstance(parameter: KParameter, inputs: Inputs): Any {
        // Create object
        //    val isCaseClass = Reflector.isDataClass(parameter.asType())
        //    val reflector = ReflectedClass(parameter.asType())
        //    val instance = if(isCaseClass){
        //      reflector.createWithDefaults(inputs)
        //    }
        //    else {
        //      reflector.updateVars(inputs)
        //    }
        //    instance
        return ""
    }
}

/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.apis.services

import kiit.apis.ApiRequest
import kiit.apis.meta.MetaDecoder
import kiit.apis.routes.Action
import kiit.apis.routes.Call
import kiit.apis.routes.MethodExecutor
import kiit.apis.routes.RouteMapping
import kiit.common.DateTime
import kiit.common.values.Inputs
import kiit.meta.KTypes
import kiit.requests.Request
import kiit.results.*
import kiit.results.builders.Outcomes
import kiit.serialization.deserializer.Deserializer
import org.json.simple.JSONObject
import kotlin.reflect.KClass


object Calls {

    val typeDefaults = mapOf(
        "String" to "",
        "Boolean" to false,
        "Int" to 0,
        "Long" to 0L,
        "Double" to 0.0,
        "DateTime" to DateTime.now()
    )


    /**
     * https://stackoverflow.com/questions/47654537/how-to-run-suspend-method-via-reflection
     */
    suspend fun callMethod(cls: KClass<*>, inst: Any, name: String, args: Array<Any?>): Any? {
        val mem = cls.members.find { m -> m.name == name }
        val params = arrayOf(inst, *args)
        val result = mem?.let {
            if(it.isSuspend) {
                kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn { cont ->
                    it.call(*params, cont)
                }
            } else {
                it.call(*params)
            }
        }
        return result
    }

    /**
     * whether or not the api call represented by the area.api.action exists. e.g. "app.users.invite"
     * and the parameters are valid.
     *
     * @param req : the command input
     * @return
     */
    fun validateCall( request: ApiRequest, allowSingleDefaultParam: Boolean = false ): Outcome<RouteMapping> {
        val req = request.request
        val fullName = req.fullName
        val args = req.data
        return request.target?.let { target ->
            val action = target.route.action
            val executor = target.handler as MethodExecutor
            val call = executor.call

            // Case 1: Single optional parameter with default value
            val res = if (allowSingleDefaultParam && call.isSingleDefaultedArg() && args.size() == 0) {
                Outcomes.success(target)
            }
            // Case 2: Request parameter - fun process(request:Request) : Outcome<String>
            else if (call.isSingleArg() && call.paramsUser.isEmpty()
                && (call.params[0].type == Call.TypeRequest || call.params[0].type == Call.TypeMeta) ) {
                Outcomes.success(target)
            }
            // Case 3: Data - check args needed
            else if (!allowSingleDefaultParam && call.hasArgs && args.size() == 0) {
                Outcomes.invalid("bad request : $fullName: inputs not supplied")
            }
            // Case 4: Data - ensure matching args
            else if (call.hasArgs) {
                val argCheck = validateArgs(request, action, call, args)
                val result = argCheck.map { target }
                result
            } else {
                Outcomes.success(target)
            }
            res
        } ?: Outcomes.errored("Unable to find action")
    }

    fun fillArgs(deserializer: Deserializer<JSONObject>, apiRef: RouteMapping, call: Call, cmd: Request): Array<Any?> {
        val action = apiRef.route.action
        // Check 1: No args ?
        return if (!call.hasArgs)
            arrayOf()
        // Check 2: 1 param with default and no args
        else if (call.isSingleDefaultedArg() && cmd.data.size() == 0) {
            val argType = call.paramsUser[0].type.toString()
            val defaultVal = if (typeDefaults.contains(argType)) typeDefaults[argType] else null
            arrayOf<Any?>(defaultVal ?: "")
        } else {
            deserializer.deserialize(call.params,cmd)
        }
    }

    /**
     * Checks that the action/method parameters are available in the inputs( json payload )
     * Given the following method and JSON payload
     *
     * 1. method  : fun add( req:Request, a:Int, b:Int ) : Int
     * 2. payload : { "a" : 1, "b": 2 }
     *
     * This validates that each parameter in the method ( "a", "b" ) exist in the payload.
     *
     * SPECIAL PARAMETERS:
     * There are some special parameters that are handled automatically without deserialization.
     *
     * 1. Request: This represents the Request itself and if exists in the parameters, is supplied
     * 2. Custom : Custom parameter builders using the request itself.
     */
    private fun validateArgs(request: ApiRequest, action: Action, call: Call, args: Inputs): Outcome<Boolean> {
        // Check each parameter to api call
        val errors = (0 until call.paramsUser.size).map { ndx ->
            val param = call.paramsUser[ndx]
            val name = param.name ?: ""
            val exists = when(param.type) {
                KTypes.KDocType -> {
                    // NOTE: For FILES:
                    // The Reading of the multi-part can be done only one time for
                    // KTor web server. This means if we load the file/doc to check
                    // for its existence then we can not read it again during loading
                    // of the values later on.
                    // One way to address this is to load / check it here and cache it.
                    // However, it could be a large file, so this is questionable.
                    // For now, we are not checking for supplied files here and instead
                    // just attempting to load them later on.
                    true
                }
                else -> args.containsKey(name)
            }
            when(exists) {
                false -> Err.on(name, "", "Missing")
                true  -> null
            }
        }
        val failures = errors.filterNotNull()
        // Any errors ?
        return if (failures.isNotEmpty()) {
            Outcomes.invalid(Err.ErrorList(failures, "Invalid request"))
        } else {
            Outcomes.success(true)
        }
    }
}

package kiit.apis.executor

import kiit.apis.ApiRequest
import kiit.apis.ApiResult
import kiit.apis.Middleware
import kiit.apis.routes.Call
import kiit.apis.routes.MethodExecutor
import kiit.meta.KTypes
import kiit.results.*
import kiit.results.builders.Outcomes
import kiit.serialization.deserializer.Deserializer
import org.json.simple.JSONObject
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * This handles execution of api actions by going through a series of steps
 * and finally invoking the associated method linked to the action. This
 * is really the core of the ApiServer in terms of processing requests.
 * The ApiServer has other checks for routes, auth, protocol.
 * Steps :
 * 1. middleware flow ( ensure all api/action middleware/policies run )
 * 2. decode metadata ( build params from any request metadata        )
 * 3. validate inputs ( method params exist in request body json      )
 * 4. invoke method   ( finally, invoke the method with the params    )
 */
class Executor(
    private val dataDecoder: Deserializer<JSONObject>,
    private val metaDecoder: MetaDecoder,
    private val middlewares: Map<String, Middleware> = mapOf()
) {

    @Suppress("UNCHECKED_CAST")
    suspend fun execute(request: ApiRequest): Outcome<ApiResult> {
        // Finally make call.
        val target = request.target
        val executor = target!!.handler as MethodExecutor
        val call = executor.call
        val result = executeFlow(request) {
            val inputs = build(request, call)
            if(!inputs.success) {
                inputs
            } else {
                val args = inputs.getOrElse { arrayOf() }
                val result = invoke(call.instance, call.member, args)
                convert(result)
            }
        }
        return result
    }

    /**
     * Ensures all middlewares are executed in the proper flow before the api action is executed.
     * This is done by doing 2 things :
     * 1. api level: Api level middlewares are applied first
     * 2. action level: Action level middlewares then applied.
     * 3. action execute: Finally, the action method execution is run.
     */
    private suspend fun executeFlow(req:ApiRequest, op:suspend (ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult> {
        val route = req.target!!

        // Level: Action ( this executes last )
        val actions = route.action.policies.map { middlewares[it] }.filterNotNull()
        val actionFlow: suspend (ApiRequest) -> Outcome<ApiResult> = { r ->
            Middleware.process(r, 0, actions, op)
        }

        // Level: API
        val global = route.api.policies.map { middlewares[it] }.filterNotNull()
        val result = Middleware.process(req, 0, global, actionFlow)
        return result
    }

    /**
     * Build inputs to the method, this includes
     * 1. Meta parameters: Request itself, or instances built from metadata ( req, user in sample below )
     * 2. Data parameters: Deserialized json data from json body ( status, durationSeconds in sample below )
     *
     * @sample {{
     *       fun updateStatus( req: Request, user:Self, status:String, durationSeconds:Int ): Outcome<Boolean> {
     *           val mobileAppVersion = req.meta["x-client-app-version")
     *           service.updateStatus(user.id, status, durationSeconds)
     *       }
     * }}
     */
    private fun build(request: ApiRequest, call: Call): Outcome<Array<Any?>> {
        val metaResult = metaDecoder.build(request)
        val inputs = metaResult.flatMap { meta ->
            val dataResult = decode(request, call)
            val combined = dataResult.map { data -> (meta.toList() + data.toList()).toTypedArray() }
            combined
        }
        return inputs
    }


    /**
     * Checks for all parameters that are NOT meta parameters( req, user in sample below )
     * Decodes the JSON body to create instances of status, and durationSeconds
     *
     * @sample {{
     *       fun updateStatus( req: Request, user:Self, status:String, durationSeconds:Int ): Outcome<Boolean> {
     *           val mobileAppVersion = req.meta["x-client-app-version")
     *           service.updateStatus(user.id, status, durationSeconds)
     *       }
     * }}
     */
    private fun decode(request: ApiRequest, call: Call): Outcome<Array<Any?>> {
        val req = request.request
        val path = request.target!!.path
        // NOTE: The meta params can be out of order!
        // Need to validate/return an error
        val params = call.params.filter { !this.metaDecoder.contains(it.type.classifier!! as KClass<*>) }
        // Arguments to the method tied to action to execute.
        val args = if (params.isEmpty()) {
            Outcomes.success(arrayOf())
        //} else if(call.hasArgs && req.data.size() == 0) {
          //  Outcomes.invalid("bad request : ${path.name}: inputs not supplied")
        } else {
            // Validate
            val checkResult = validate(request, params)
            val inputs = checkResult.map { dataDecoder.deserialize(params, request.request) }
            inputs
        }
        return args
    }


    /**
     * Validates that the data parameters, ( status and durationSeconds in sample below ),
     * exist in the request JSON body, and if not, populates an error collection to return.
     *
     * @sample {{
     *       fun updateStatus( req: Request, user:Self, status:String, durationSeconds:Int ): Outcome<Boolean> {
     *           val mobileAppVersion = req.meta["x-client-app-version")
     *           service.updateStatus(user.id, status, durationSeconds)
     *       }
     * }}
     */
    private fun validate(request: ApiRequest, params:List<KParameter>): Outcome<Boolean> {
        val req = request.request
        // Check each parameter to api call
        val errors = (0 until params.size).map { ndx ->
            val param = params[ndx]
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
                else -> req.data.containsKey(name)
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


    /**
     * Ensures that the result of any action/method call is wrapped an Outcome.
     * This is because this is the enforced contract / standardized response
     * from any api/action call. For example: Given a Int output, it is wrapped
     *
     * fun add(a:Int, b:Int) : Int = a + b
     * val result = add(1, 2)
     * Outcomes.success(result)
     */
    private fun convert(output:Any?):Outcome<ApiResult> {
        val wrapped = output?.let { res ->
            if (res is Result<*, *>) {
                (res as Result<ApiResult, Err>)
            } else {
                Outcomes.of(res!!)
            }
        } ?: Outcomes.of(Exception("Received null"))
        return wrapped
    }


    companion object {
        /**
         * https://stackoverflow.com/questions/47654537/how-to-run-suspend-method-via-reflection
         */
        suspend fun invoke(instance: Any, member: KCallable<*>, args: Array<Any?>): Any? {
            val params = arrayOf(instance, *args)
            val result = if (member.isSuspend) {
                kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn { cont ->
                    member.call(*params, cont)
                }
            } else {
                member.call(*params)
            }
            return result
        }
    }
}

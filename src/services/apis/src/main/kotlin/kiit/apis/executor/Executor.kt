package kiit.apis.executor

import kiit.apis.ApiRequest
import kiit.apis.ApiResult
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
 */
class Executor(
    private val dataDecoder: Deserializer<JSONObject>,
    private val metaDecoder: MetaDecoder
) {

    @Suppress("UNCHECKED_CAST")
    suspend fun execute(request: ApiRequest): Outcome<ApiResult> {
        // Finally make call.
        val target = request.target
        val executor = target!!.handler as MethodExecutor
        val call = executor.call
        val inputs = build(request, call)
        if(!inputs.success) return inputs
        val output = invoke(call.instance, call.member, inputs.getOrElse { arrayOf() })
        val wrapped = output?.let { res ->
            if (res is Result<*, *>) {
                (res as Result<ApiResult, Err>)
            } else {
                Outcomes.of(res!!)
            }
        } ?: Outcomes.of(Exception("Received null"))
        return wrapped
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
        } else if(call.hasArgs && req.data.size() == 0) {
            Outcomes.invalid("bad request : ${path.name}: inputs not supplied")
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

    companion object {
        /**
         * https://stackoverflow.com/questions/47654537/how-to-run-suspend-method-via-reflection
         */
        public suspend fun invoke(instance: Any, member: KCallable<*>, args: Array<Any?>): Any? {
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

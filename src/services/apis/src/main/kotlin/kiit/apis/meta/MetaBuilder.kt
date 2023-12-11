package kiit.apis.meta

import kiit.apis.ApiRequest
import kiit.apis.routes.MethodExecutor
import kotlin.reflect.KClass
import kotlin.reflect.KParameter


/**
 * Builds or deserializes instances of any "meta parameters" that were supplied in the Request itself or its metadata.
 * A "Meta Parameter" is a parameter with a data type that is created from metadata in the Request.
 * This is analogous to a Deserializer for the Request JSON Body, but for the Request metadata.
 * For example, given action/method below
 *
 * @sample {{
 *      fun changeDisplayName( req: Request, user:Self, displayName:String ): Outcome<Boolean> {
 *          val mobileAppVersion = req.meta["x-client-app-version")
 *          service.changeDisplayName(user.id, displayName)
 *      }
 * }}
 *
 * Meta Parameters
 * These are the meta parameters
 * 1. Request::class, { req:ApiRequest -> req  }
 * 2. Self::class   , { req:ApiRequest -> Self( JWT.parse(req.meta["Authorization"]).subject )
 *
 * This allows the server to build the instances and pass them off as values to the method to execute.
 */
class MetaBuilder(private val metas: Map<String, MetaDecoder>) {

    /**
     * Processes the request by building instances of any meta parameters.
     */
    fun build(request: ApiRequest): Array<Any?> {
        return request.target?.let { t  ->
            when(t.handler) {
                // Check Action parameters that match type
                is MethodExecutor -> {
                    val executor = t.handler as MethodExecutor
                    val matching = filter(executor.call.params)
                    val built = create(request, matching)
                    built
                }
                else -> arrayOf()
            }
        } ?: arrayOf()
    }


    /**
     * Filters the parameters by picking the ones that contain
     * data types matching the Meta Decoders
     */
    private fun filter(parameters:List<KParameter>):List<KParameter> {
        val matching = parameters.filter {
            when(val kls = it.type.classifier) {
                null -> false
                else -> metas.containsKey((kls as KClass<*>).qualifiedName)
            }
        }
        return matching
    }


    /**
     * Takes the meta parameters (data types that can be built from metadata )
     * and actually creates the instances using the MetaDecoders for the associated type.
     */
    private fun create(request: ApiRequest, metaParams: List<KParameter>): Array<Any?> {
        return when(metaParams.isEmpty()) {
            true  -> arrayOf()
            false -> metaParams.map {
                val kls = it.type.classifier as KClass<*>
                val result = metas[kls.qualifiedName]!!.op(request)
                result
            }.toTypedArray()
        }
    }
}

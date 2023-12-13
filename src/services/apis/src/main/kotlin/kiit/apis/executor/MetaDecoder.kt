package kiit.apis.executor

import kiit.apis.ApiRequest
import kiit.apis.routes.Call
import kiit.apis.routes.MethodExecutor
import kiit.common.values.Metadata
import kiit.requests.Request
import kiit.results.Outcome
import kiit.results.builders.Outcomes
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
class MetaDecoder(val metas: List<Pair<KClass<*>, MetaHandler>>) {
    private val mapping: Map<String, MetaHandler> = of(metas)

    /**
     * Whether or not the meta parameters containd the datatype supplied
     */
    fun contains(cls:KClass<*>): Boolean = mapping.containsKey(cls.qualifiedName)

    /**
     * Processes the request by building instances of any meta parameters.
     */
    fun build(request: ApiRequest): Outcome<Array<Any?>> {
        return request.target?.let { t  ->
            when(t.handler) {
                // Check Action parameters that match type
                is MethodExecutor -> {
                    val executor = t.handler as MethodExecutor
                    val matching = filter(executor.call.params)
                    val built = create(request, matching)
                    Outcomes.success(built)
                }
                else -> Outcomes.success(arrayOf())
            }
        } ?: Outcomes.success(arrayOf())
    }


    /**
     * Filters the parameters by picking the ones that contain
     * data types matching the Meta Decoders
     */
    private fun filter(parameters:List<KParameter>):List<KParameter> {
        val matching = parameters.filter {
            when(val kls = it.type.classifier) {
                null -> false
                else -> mapping.containsKey((kls as KClass<*>).qualifiedName)
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
                val metahandler = mapping[kls.qualifiedName]!!
                val result = metahandler(request)
                result
            }.toTypedArray()
        }
    }

    companion object {

        /**
         * Builds a map of Data type qualified name to the MetaDecoder
         * Examples:
         * 1. Request::class.qualifiedName to MetaDecoder(Request::class, (ApiRequest) -> Any?)
         * 2. Self::class.qualifiedName    to MetaDecoder(Self::class   , (ApiRequest) -> Any?)
         */
        fun of(mappings: List<Pair<KClass<*>, MetaHandler>>) : Map<String, MetaHandler> {
            val metas = when(mappings.isEmpty()) {
                true -> builtins
                false -> build(mappings)
            }
            val decoders = metas.map { Pair(it.first.qualifiedName!!, it.second) }
            return decoders.toMap()
        }


        private val builtins:List<Pair<KClass<*>, MetaHandler>> by lazy {
            listOf(
                Pair(Request::class) { req: ApiRequest -> req.request },
                Pair(Metadata::class) { req: ApiRequest -> req.request.meta }
            )
        }


        fun build(mappings: List<Pair<KClass<*>, MetaHandler>>):List<Pair<KClass<*>, MetaHandler>> {
            // Add Request::class and Metadata::class automatically.
            val hasRequest = mappings.any { it.first == Call.TypeRequest }
            val hasMetadata = mappings.any { it.first == Call.TypeMeta   }
            val finalMappings = mutableListOf<Pair<KClass<*>, MetaHandler>>()
            if(!hasRequest) {
                finalMappings.add(Pair(Request::class) { req: ApiRequest -> req.request })
            }
            if(!hasMetadata) {
                finalMappings.add(Pair(Metadata::class) { req: ApiRequest -> req.request.meta })
            }
            return finalMappings
        }
    }
}

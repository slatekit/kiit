package kiit.apis.meta

import kiit.apis.ApiRequest
import kotlin.reflect.KClass


/**
 * Simple wrapper to store and map the datatype to a MetaHandler.
 * Used for cases below
 * 1. Request::class, { req:ApiRequest -> req  }
 * 2. Self::class   , { req:ApiRequest -> Self( JWT.parse(req.meta["Authorization"]).subject )
 */
class MetaDecoder(val mtype: KClass<*>, val op:MetaHandler) {
    fun build(request: ApiRequest) : Any? {
        return op(request)
    }

    companion object {

        /**
         * Builds a map of Data type qualified name to the MetaDecoder
         * Examples:
         * 1. Request::class.qualifiedName to MetaDecoder(Request::class, (ApiRequest) -> Any?)
         * 2. Self::class.qualifiedName    to MetaDecoder(Self::class   , (ApiRequest) -> Any?)
         */
        fun of(mappings: List<Pair<KClass<*>, MetaHandler>>) : Map<String, MetaDecoder> {
            val metas = when(mappings.isEmpty()) {
                true -> MetaUtils.builtins
                false -> MetaUtils.build(mappings)
            }
            val decoders = metas.map { Pair(it.first.qualifiedName!!, MetaDecoder(it.first, it.second)) }
            return decoders.toMap()
        }
    }
}

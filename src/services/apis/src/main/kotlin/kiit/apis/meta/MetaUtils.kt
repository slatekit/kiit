package kiit.apis.meta

import kiit.apis.ApiRequest
import kiit.apis.routes.Call
import kiit.common.values.Metadata
import kiit.requests.Request
import kotlin.reflect.KClass

object MetaUtils {

    val builtins:List<Pair<KClass<*>, MetaHandler>> by lazy {
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

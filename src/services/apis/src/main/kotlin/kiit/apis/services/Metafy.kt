package kiit.apis.services

import kiit.apis.ApiRequest
import kotlin.reflect.KClass


/**
 * Takes an API Request and builds a resulting object from the request metadata.
 * This is used for decoded metadata into some data type.
 * Examples:
 * 1. Request: Take the Request itself and return it ( for parameters to actions/methods )
 * 2. Self   : Take the JWT from "Authorization" and build Self type containing user id
 */
typealias MetaHandler = (ApiRequest) -> Any?


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
            return mappings.map { Pair(it.first.qualifiedName!!, MetaDecoder(it.first, it.second)) }.toMap()
        }
    }
}

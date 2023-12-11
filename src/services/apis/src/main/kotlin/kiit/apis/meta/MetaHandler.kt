package kiit.apis.meta

import kiit.apis.ApiRequest

/**
 * Takes an API Request and builds a resulting object from the request metadata.
 * This is used for decoded metadata into some data type.
 * Examples:
 * 1. Request: Take the Request itself and return it ( for parameters to actions/methods )
 * 2. Self   : Take the JWT from "Authorization" and build Self type containing user id
 */
typealias MetaHandler = (ApiRequest) -> Any?


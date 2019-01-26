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

package slatekit.results

/**
 * Minimal subset of http status codes to be used as general purpose codes.
 *
 * DESIGN: One may think that http status code should have no connection
 * outside of an Http context. The http codes in a services/logic layer
 * could be considered an implementation leak. However, consider the following:
 *
 * 1. Many of the http status code are actual quite general purpose.
 *    Such as Ok, BadRequest, Unexpected Error, Unauthorized, etc.
 *
 * 2. They are purposely designed to be extendable
 *    So you can for example create custom codes in the 1000+ range
 *
 * 3. There is a similarity of existing Java / Kotlin exceptions that
 *    map quite nicely to status codes. These include:
 *    Java                     Http
 *    ArgumentException        Bad_Request
 *    SecurityException        Unauthorized
 *    NotImplementedError      NotImplemented
 *    Exception                Server_Error
 *
 * NOTES:
 * You can also use any codes in the Result<S, F> component which models
 * successes and failures. Then just before you return an response at your
 * application boundary ( such as an Http Endpoint ), you can then convert
 * your application code to a Http Status code.
 *
 * The point is you can use any codes you want and some of the http status
 * code are ( on a philosophical level ) considered general purpose in slatekit.
 *
 */

object Codes {
    val SUCCESS          = Successful (200, "Success")
    val CONFIRM          = Successful (230, "Confirmation needed")


    val FAILURE          = Invalid (400, "Failure")
    val BAD_REQUEST      = Invalid (400, "Bad request")
    val UNAUTHORIZED     = Invalid (401, "Unauthorized")
    val FORBIDDEN        = Invalid (403, "Forbidden")
    val NOT_FOUND        = Invalid (404, "Not found")
    val CONFLICT         = Invalid (409, "Conflict")
    val DEPRECATED       = Invalid (426, "Deprecated")
    val NOT_IMPLEMENTED  = Invalid(501, "Not implemented")
    val NOT_AVAILABLE    = Invalid(503, "Not available")


    val FILTERED         = Filtered(450, "Filtered")


    val UNEXPECTED = Unexpected(500, "Unexpected error")
}

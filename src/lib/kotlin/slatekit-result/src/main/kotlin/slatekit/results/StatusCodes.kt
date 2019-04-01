/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * philosophy: Simplicity above all else
 * </slate_header>
 */

package slatekit.results


import slatekit.results.StatusGroup.Succeeded
import slatekit.results.StatusGroup.Pending
import slatekit.results.StatusGroup.Denied
import slatekit.results.StatusGroup.Invalid
import slatekit.results.StatusGroup.Ignored
import slatekit.results.StatusGroup.Errored
import slatekit.results.StatusGroup.Unhandled

/**
 * Default set of status and error codes available from this library
 *
 * # GOALS:
 * 1. General purpose enough and can be used to model most successes/filters/errors
 * 2. Completely OPTIONAL to use but here for convenience
 * 3. Used as default codes for the [Results] builder methods
 * 4. Can be used at almost any layer of your application ( controller/service/utils etc )
 * 5. HTTP support via utility methods below to convert codes to compatible HTTP codes
 *
 * # NOTES:
 * 1. [JvmField] is applied for Java Interop to access the value as a static field e.g. `Codes.SUCCESS`
 * 2. [StatusGroup] extends [Status] and is used for default implementations of [Status]
 * 3. [StatusGroup] also has logical grouping of errors
 */
object StatusCodes {

    // Success: 1000 + range
    @JvmField val SUCCESS         = Succeeded(1001, "Success")
    @JvmField val CREATED         = Succeeded(1002, "Created")
    @JvmField val UPDATED         = Succeeded(1003, "Updated")
    @JvmField val FETCHED         = Succeeded(1004, "Fetched")
    @JvmField val PATCHED         = Succeeded(1005, "Patched")         // E.g. Update a small subset of info
    @JvmField val DELETED         = Succeeded(1006, "Deleted")
    @JvmField val HANDLED         = Succeeded(1007, "Handled")         // E.g. A silent ok ( similar to http 204 )
    @JvmField val PENDING         = Pending  (1008, "Pending")
    @JvmField val QUEUED          = Pending  (1009, "Queued" )
    @JvmField val CONFIRM         = Pending  (1010, "Confirm")

    // Invalid: 2000 + range
    @JvmField val IGNORED         = Ignored  (2001, "Ignored")         // E.g. Ignored, not exactly an error
    @JvmField val BAD_REQUEST     = Invalid  (2002, "Bad Request")     // E.g. Invalid JSON
    @JvmField val INVALID         = Invalid  (2003, "Invalid")         // E.g. Valid   JSON but invalid values
    @JvmField val DENIED          = Denied   (2004, "Denied")          // Presumes a checked condition
    @JvmField val UNSUPPORTED     = Denied   (2005, "Not supported")   // Presumes a checked condition
    @JvmField val UNIMPLEMENTED   = Denied   (2006, "Not implemented") // Presumes a checked condition
    @JvmField val UNAVAILABLE     = Denied   (2007, "Not available")   // Presumes a checked condition
    @JvmField val UNAUTHENTICATED = Denied   (2008, "Unauthenticated") // Presumes a checked condition
    @JvmField val UNAUTHORIZED    = Denied   (2009, "Unauthorized")    // Presumes a checked condition

    // Errors: 3000 + range
    @JvmField val NOT_FOUND       = Errored  (3001, "Not found")       // E.g. Resource/End point not found
    @JvmField val MISSING         = Errored  (3002, "Missing item")    // E.g. Domain model not found
    @JvmField val FORBIDDEN       = Errored  (3003, "Forbidden")
    @JvmField val CONFLICT        = Errored  (3004, "Conflict")
    @JvmField val DEPRECATED      = Errored  (3005, "Deprecated")
    @JvmField val TIMEOUT         = Errored  (3006, "Timeout")
    @JvmField val ERRORED         = Errored  (3007, "Errored")         // General purpose use
    @JvmField val UNEXPECTED      = Unhandled(3008, "Unexpected")

    // Success ( Interactive / Metadata )
    @JvmField val EXIT            = Succeeded(4001, "Exiting")
    @JvmField val HELP            = Succeeded(4002, "Help")
    @JvmField val ABOUT           = Succeeded(4003, "About")
    @JvmField val VERSION         = Succeeded(4004, "Version")


    /**
     * Converts a code to a compatible http status with code
     * @return Triple with http status code, original code, and optional exception
     */
    @JvmStatic fun toHttp(code: Status):Pair<Int, Status> {
        return when(code) {
            is StatusGroup -> convert(code)
            else         -> Pair( code.code, code)
        }
    }


    /**
     * Converts a code to a compatible http status with code
     * @return Triple with http status code, original code, and optional exception
     */
    @JvmStatic fun fromHttp(code: Int):Status {
        return when(code) {
            SUCCESS.code  -> SUCCESS
            CREATED.code  -> CREATED
            UPDATED.code  -> UPDATED
            FETCHED.code  -> FETCHED
            PATCHED.code  -> PATCHED
            DELETED.code  -> DELETED
            PENDING.code  -> PENDING
            QUEUED.code   -> QUEUED
            HANDLED.code  -> HANDLED
            CONFIRM.code  -> CONFIRM

            // Info
            HELP.code     -> HELP
            ABOUT.code    -> ABOUT
            VERSION.code  -> VERSION

            // Invalid
            IGNORED.code       -> IGNORED
            BAD_REQUEST.code   -> BAD_REQUEST
            INVALID.code       -> INVALID
            UNSUPPORTED.code   -> UNSUPPORTED
            UNIMPLEMENTED.code -> UNIMPLEMENTED
            UNAVAILABLE.code   -> UNAVAILABLE

            // Errors
            MISSING.code          -> MISSING
            NOT_FOUND.code        -> NOT_FOUND
            UNAUTHENTICATED.code  -> UNAUTHENTICATED
            UNAUTHORIZED.code     -> UNAUTHORIZED
            FORBIDDEN.code        -> FORBIDDEN
            TIMEOUT.code          -> TIMEOUT
            CONFLICT.code         -> CONFLICT
            DEPRECATED.code       -> DEPRECATED
            ERRORED.code          -> ERRORED
            UNEXPECTED.code       -> UNEXPECTED
            EXIT.code             -> EXIT

            else                  -> {
                when {
                    code in 1..999 -> Succeeded(code, SUCCESS.msg)
                    code in 2000..2999 -> Invalid(code, INVALID.msg)
                    code >= 3000 -> Errored(code, ERRORED.msg)
                    else -> Errored(code, "Unexpected")
                }
            }
        }
    }


    /**
     * Converts a status to a compatible http status code
     * @return Triple with http status code, original code, and optional exception
     */
    @JvmStatic fun convert(status: StatusGroup):Pair<Int, Status> {
        return when(status.code) {
            // Success
            SUCCESS.code  -> Pair(200, status)
            CREATED.code  -> Pair(201, status)
            UPDATED.code  -> Pair(200, status)
            FETCHED.code  -> Pair(200, status)
            PATCHED.code  -> Pair(200, status)
            DELETED.code  -> Pair(200, status)
            PENDING.code  -> Pair(202, status)
            QUEUED.code   -> Pair(202, status)
            HANDLED.code  -> Pair(204, status)
            CONFIRM.code  -> Pair(200, status)

            // Info
            HELP.code     -> Pair(200, status)
            ABOUT.code    -> Pair(200, status)
            VERSION.code  -> Pair(200, status)

            // Invalid
            IGNORED.code       -> Pair(400, status)
            BAD_REQUEST.code   -> Pair(400, status)
            INVALID.code       -> Pair(400, status)
            UNSUPPORTED.code   -> Pair(501, status)
            UNIMPLEMENTED.code -> Pair(501, status)
            UNAVAILABLE.code   -> Pair(503, status)

            // Errors
            MISSING.code          -> Pair(400, status)
            NOT_FOUND.code        -> Pair(404, status)
            UNAUTHENTICATED.code  -> Pair(401, status)
            UNAUTHORIZED.code     -> Pair(401, status)
            FORBIDDEN.code        -> Pair(403, status)
            TIMEOUT.code          -> Pair(408, status)
            CONFLICT.code         -> Pair(409, status)
            DEPRECATED.code       -> Pair(426, status)
            ERRORED.code          -> Pair(500, status)
            UNEXPECTED.code       -> Pair(500, status)
            EXIT.code             -> Pair(503, status)

            else                  -> {
                when(status) {
                    is HttpCode -> Pair((status as HttpCode).toHttpCode(), status as Status)
                    else        -> Pair(status.code, status)
                }
            }
        }
    }
}

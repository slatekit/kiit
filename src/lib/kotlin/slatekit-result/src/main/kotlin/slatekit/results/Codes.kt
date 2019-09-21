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


import slatekit.results.Status.Succeeded
import slatekit.results.Status.Pending
import slatekit.results.Status.Denied
import slatekit.results.Status.Invalid
import slatekit.results.Status.Ignored
import slatekit.results.Status.Errored
import slatekit.results.Status.Unexpected

/**
 * Default set of status and error codes available from this library
 *
 * # GOALS:
 * 1. General purpose enough and can be used to model most successes/filters/errors
 * 2. Completely OPTIONAL to use but here for convenience
 * 3. Used as default codes for the [Result] builder methods
 * 4. Can be used at almost any layer of your application ( controller/service/utils etc )
 * 5. HTTP support via utility methods below to convert codes to compatible HTTP codes
 *
 * # NOTES:
 * 1. [JvmField] is applied for Java Interop to access the value as a static field e.g. `Codes.SUCCESS`
 * 2. [Status] extends [Status] and is used for default implementations of [Status]
 * 3. [Status] also has logical grouping of errors
 */
object Codes {

    // Success: 200000 + range
    @JvmField val SUCCESS         = Succeeded (200001, "Success")
    @JvmField val CREATED         = Succeeded (200002, "Created")
    @JvmField val UPDATED         = Succeeded (200003, "Updated")
    @JvmField val FETCHED         = Succeeded (200004, "Fetched")
    @JvmField val PATCHED         = Succeeded (200005, "Patched")         // E.g. Update a small subset of info
    @JvmField val DELETED         = Succeeded (200006, "Deleted")
    @JvmField val HANDLED         = Succeeded (200007, "Handled")         // E.g. A silent ok ( similar to http 204 )
    @JvmField val PENDING         = Pending   (200008, "Pending")
    @JvmField val QUEUED          = Pending   (200009, "Queued" )
    @JvmField val CONFIRM         = Pending   (200010, "Confirm")

    // Invalid: 400000 + range
    @JvmField val IGNORED         = Ignored   (400001, "Ignored")         // E.g. Ignored, not exactly an error
    @JvmField val BAD_REQUEST     = Invalid   (400002, "Bad Request")     // E.g. Invalid JSON
    @JvmField val INVALID         = Invalid   (400003, "Invalid")         // E.g. Valid   JSON but invalid values
    @JvmField val DENIED          = Denied    (400004, "Denied")          // Presumes a checked condition
    @JvmField val UNSUPPORTED     = Denied    (400005, "Not supported")   // Presumes a checked condition
    @JvmField val UNIMPLEMENTED   = Denied    (400006, "Not implemented") // Presumes a checked condition
    @JvmField val UNAVAILABLE     = Denied    (400007, "Not available")   // Presumes a checked condition
    @JvmField val UNAUTHENTICATED = Denied    (400008, "Unauthenticated") // Presumes a checked condition
    @JvmField val UNAUTHORIZED    = Denied    (400009, "Unauthorized")    // Presumes a checked condition

    // Errors: 500000 + range
    @JvmField val NOT_FOUND       = Errored   (500001, "Not found")       // E.g. Resource/End point not found
    @JvmField val MISSING         = Errored   (500002, "Missing item")    // E.g. Domain model not found
    @JvmField val FORBIDDEN       = Errored   (500003, "Forbidden")
    @JvmField val CONFLICT        = Errored   (500004, "Conflict")
    @JvmField val DEPRECATED      = Errored   (500005, "Deprecated")
    @JvmField val TIMEOUT         = Errored   (500006, "Timeout")
    @JvmField val ERRORED         = Errored   (500007, "Errored")         // General purpose use
    @JvmField val UNEXPECTED      = Unexpected(500008, "Unexpected")

    // Success ( Interactive / Metadata )
    @JvmField val EXIT            = Succeeded (600001, "Exiting")
    @JvmField val HELP            = Succeeded (600002, "Help")
    @JvmField val ABOUT           = Succeeded (600003, "About")
    @JvmField val VERSION         = Succeeded (600004, "Version")


    private val mappings = listOf(
            Triple(SUCCESS.code          , SUCCESS          , 200),
            Triple(CREATED.code          , CREATED          , 201),
            Triple(UPDATED.code          , UPDATED          , 200),
            Triple(FETCHED.code          , FETCHED          , 200),
            Triple(PATCHED.code          , PATCHED          , 200),
            Triple(DELETED.code          , DELETED          , 200),
            Triple(PENDING.code          , PENDING          , 202),
            Triple(QUEUED .code          , QUEUED           , 202),
            Triple(HANDLED.code          , HANDLED          , 204),
            Triple(CONFIRM.code          , CONFIRM          , 200),

            // Info
            Triple(HELP.code             , HELP             , 200),
            Triple(ABOUT.code            , ABOUT            , 200),
            Triple(VERSION.code          , VERSION          , 200),

            // Invalid
            Triple(IGNORED.code          , IGNORED          , 400),
            Triple(BAD_REQUEST.code      , BAD_REQUEST      , 400),
            Triple(INVALID.code          , INVALID          , 400),
            Triple(UNSUPPORTED.code      , UNSUPPORTED      , 501),
            Triple(UNIMPLEMENTED.code    , UNIMPLEMENTED    , 501),
            Triple(UNAVAILABLE.code      , UNAVAILABLE      , 503),

            // Errors
            Triple(MISSING.code          , MISSING          , 400),
            Triple(NOT_FOUND.code        , NOT_FOUND        , 404),
            Triple(DENIED.code           , DENIED           , 401),
            Triple(UNAUTHENTICATED.code  , UNAUTHENTICATED  , 401),
            Triple(UNAUTHORIZED.code     , UNAUTHORIZED     , 401),
            Triple(FORBIDDEN.code        , FORBIDDEN        , 403),
            Triple(TIMEOUT.code          , TIMEOUT          , 408),
            Triple(CONFLICT.code         , CONFLICT         , 409),
            Triple(DEPRECATED.code       , DEPRECATED       , 426),
            Triple(ERRORED.code          , ERRORED          , 500),
            Triple(UNEXPECTED.code       , UNEXPECTED       , 500),
            Triple(EXIT.code             , EXIT             , 503)
    )

    private val lookupHttp = mappings.map{ Pair(it.first, it) }.toMap()
    private val lookupCode = mappings.map{ Pair(it.third, it) }.toMap()


    /**
     * Converts a status to a compatible http status code
     * @return Triple with http status code, original code, and optional exception
     */
    @JvmStatic fun toHttp(status: Status):Pair<Int, Status> {
        val exists = lookupHttp.containsKey(status.code)
        return when(exists){
            true -> {
                val httpCode = lookupHttp[status.code]
                Pair(httpCode?.third ?: 400, status)
            }
            else -> when(status) {
                is HttpCode -> Pair((status as HttpCode).toHttpCode(), status as Status)
                else        -> Pair(status.code, status)
            }
        }
    }


    /**
     * Converts a code to a compatible http status with code
     * @return Triple with http status code, original code, and optional exception
     */
    @JvmStatic fun fromHttp(code: Int):Status {
        val exists = lookupCode.containsKey(code)
        return when(exists){
            true -> {
                val info = lookupCode[code]
                info?.second ?: BAD_REQUEST
            }
            else -> when {
                code in 1..999 -> Succeeded(code, SUCCESS.msg)
                code in 2000..2999 -> Invalid(code, INVALID.msg)
                code >= 3000 -> Errored(code, ERRORED.msg)
                else -> Errored(code, "Unexpected")
            }
        }
    }
}

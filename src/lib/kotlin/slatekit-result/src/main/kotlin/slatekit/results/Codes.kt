/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A Kotlin Tool-Kit for Server + Android
 * </slate_header>
 */

/* ktlint-disable */
package slatekit.results



/**
 * Default set of status codes
 *
 * # GOALS:
 * 1. General purpose enough and can be used to model most logical groups of successes/failures
 * 2. Completely OPTIONAL to use but here for convenience
 * 3. Used as default codes for the [Result] builder methods
 * 4. Can be used at almost any layer of your application ( controller/service/utils etc )
 * 5. HTTP support via utility methods below to convert codes to compatible HTTP codes
 *
 * # NOTES:
 * 1. [JvmField] is applied for Java Interop to access the value as a static field e.g. `Codes.SUCCESS`
 * 2. [Status] has logical grouping of errors
 */
object Codes {

    // Success: 200000 + range
    @JvmField val SUCCESS         = Passed.Succeeded (200001, "Success")
    @JvmField val CREATED         = Passed.Succeeded (200002, "Created")
    @JvmField val UPDATED         = Passed.Succeeded (200003, "Updated")
    @JvmField val FETCHED         = Passed.Succeeded (200004, "Fetched")
    @JvmField val PATCHED         = Passed.Succeeded (200005, "Patched")         // E.g. Update a small subset of info
    @JvmField val DELETED         = Passed.Succeeded (200006, "Deleted")
    @JvmField val HANDLED         = Passed.Succeeded (200007, "Handled")         // E.g. A silent ok ( similar to http 204 )
    @JvmField val PENDING         = Passed.Pending   (200008, "Pending")
    @JvmField val QUEUED          = Passed.Pending   (200009, "Queued" )
    @JvmField val CONFIRM         = Passed.Pending   (200010, "Confirm")


    // Invalid: 400000 + range
    @JvmField val IGNORED         = Failed.Ignored   (400001, "Ignored")         // E.g. Ignored, not exactly an error
    @JvmField val BAD_REQUEST     = Failed.Invalid   (400002, "Bad Request")     // E.g. Invalid JSON
    @JvmField val INVALID         = Failed.Invalid   (400003, "Invalid")         // E.g. Valid   JSON but invalid values

    // Security related
    @JvmField val DENIED          = Failed.Denied    (400004, "Denied")          // Presumes a checked condition
    @JvmField val UNSUPPORTED     = Failed.Denied    (400005, "Not supported")   // Presumes a checked condition
    @JvmField val UNIMPLEMENTED   = Failed.Denied    (400006, "Not implemented") // Presumes a checked condition
    @JvmField val UNAVAILABLE     = Failed.Denied    (400007, "Not available")   // Presumes a checked condition
    @JvmField val UNAUTHENTICATED = Failed.Denied    (400008, "Unauthenticated") // Presumes a checked condition
    @JvmField val UNAUTHORIZED    = Failed.Denied    (400009, "Unauthorized")    // Presumes a checked condition

    // Expected errors: 500000 + range
    @JvmField val NOT_FOUND       = Failed.Errored   (500001, "Not found")       // E.g. Resource/End point not found
    @JvmField val MISSING         = Failed.Errored   (500002, "Missing item")    // E.g. Domain model not found
    @JvmField val FORBIDDEN       = Failed.Errored   (500003, "Forbidden")
    @JvmField val CONFLICT        = Failed.Errored   (500004, "Conflict")
    @JvmField val DEPRECATED      = Failed.Errored   (500005, "Deprecated")
    @JvmField val TIMEOUT         = Failed.Errored   (500006, "Timeout")
    @JvmField val ERRORED         = Failed.Errored   (500007, "Errored")         // General purpose use
    @JvmField val LIMITED         = Failed.Errored   (500009, "Limited")

    // Unexpected
    @JvmField val UNEXPECTED      = Failed.Unknown(500008, "Unexpected")

    // Success ( Interactive / Metadata )
    @JvmField val EXIT            = Passed.Succeeded (600002, "Exiting")
    @JvmField val HELP            = Passed.Succeeded (600003, "Help")
    @JvmField val ABOUT           = Passed.Succeeded (600004, "About")
    @JvmField val VERSION         = Passed.Succeeded (600005, "Version")


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
                code in 1..999 -> Passed.Succeeded(code, SUCCESS.msg)
                code in 2000..2999 -> Failed.Invalid(code, INVALID.msg)
                code >= 3000 -> Failed.Errored(code, ERRORED.msg)
                else -> Failed.Errored(code, "Unexpected")
            }
        }
    }
}

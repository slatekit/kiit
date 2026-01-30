/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A Kotlin Tool-Kit for Server + Android
 *  </kiit_header>
 */

/* ktlint-disable */
package kiit.results



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

    // Success: 200000 + range ( useful for CRUD operations )
    @JvmField val SUCCESS         = Passed.Succeeded ("SUCCESS", 200001, "Success")
    @JvmField val CREATED         = Passed.Succeeded ("CREATED", 200002, "Created")
    @JvmField val UPDATED         = Passed.Succeeded ("UPDATED", 200003, "Updated")
    @JvmField val FETCHED         = Passed.Succeeded ("FETCHED", 200004, "Fetched")
    @JvmField val PATCHED         = Passed.Succeeded ("PATCHED", 200005, "Patched")         // E.g. Update a small subset of info
    @JvmField val DELETED         = Passed.Succeeded ("DELETED", 200006, "Deleted")
    @JvmField val HANDLED         = Passed.Succeeded ("HANDLED", 200007, "Handled")         // E.g. A silent ok ( similar to http 204 )
    @JvmField val PENDING         = Passed.Pending   ("PENDING", 200008, "Pending")
    @JvmField val QUEUED          = Passed.Pending   ("QUEUED" , 200009, "Queued" )
    @JvmField val CONFIRM         = Passed.Pending   ("CONFIRM", 200010, "Confirm")

    // Success: 200000 + range ( useful for JOB States )
    @JvmField val ACTIVE          = Passed.Pending   ("ACTIVE"  , 200101, "Active"  )
    @JvmField val INACTIVE        = Passed.Pending   ("INACTIVE", 200102, "Inactive")
    @JvmField val STARTING        = Passed.Pending   ("STARTING", 200103, "Starting")
    @JvmField val WAITING         = Passed.Pending   ("WAITING" , 200104, "Waiting" )
    @JvmField val RUNNING         = Passed.Pending   ("RUNNING" , 200105, "Running" )
    @JvmField val PAUSED          = Passed.Pending   ("PAUSED"  , 200106, "Paused"  )
    @JvmField val STOPPED         = Passed.Pending   ("STOPPED" , 200107, "Stopped" )
    @JvmField val COMPLETE        = Passed.Pending   ("COMPLETE", 200108, "Complete")


    // Invalid: 400000 + range
    @JvmField val IGNORED         = Failed.Ignored   ("IGNORED"    , 400001, "Ignored")         // E.g. Ignored, not exactly an error
    @JvmField val BAD_REQUEST     = Failed.Invalid   ("BAD_REQUEST", 400002, "Bad Request")     // E.g. Invalid JSON
    @JvmField val INVALID         = Failed.Invalid   ("INVALID"    , 400003, "Invalid")         // E.g. Valid   JSON but invalid values
    @JvmField val NOT_FOUND       = Failed.Invalid   ("NOT_FOUND"  , 400004, "Not found")       // E.g. Resource/End point not found

    // Security related
    @JvmField val DENIED          = Failed.Denied    ("DENIED"         ,400005, "Denied")          // Presumes a checked condition
    @JvmField val UNSUPPORTED     = Failed.Denied    ("UNSUPPORTED"    ,400006, "Not supported")   // Presumes a checked condition
    @JvmField val UNIMPLEMENTED   = Failed.Denied    ("UNIMPLEMENTED"  ,400007, "Not implemented") // Presumes a checked condition
    @JvmField val UNAVAILABLE     = Failed.Denied    ("UNAVAILABLE"    ,400008, "Not available")   // Presumes a checked condition
    @JvmField val UNAUTHENTICATED = Failed.Denied    ("UNAUTHENTICATED",400009, "Unauthenticated") // Presumes a checked condition
    @JvmField val UNAUTHORIZED    = Failed.Denied    ("UNAUTHORIZED"   ,400010, "Unauthorized")    // Presumes a checked condition

    // Expected errors: 500000 + range
    @JvmField val MISSING         = Failed.Errored   ("MISSING"   ,500002, "Missing item")    // E.g. Domain model not found
    @JvmField val FORBIDDEN       = Failed.Errored   ("FORBIDDEN" ,500003, "Forbidden")
    @JvmField val CONFLICT        = Failed.Errored   ("CONFLICT"  ,500004, "Conflict")
    @JvmField val DEPRECATED      = Failed.Errored   ("DEPRECATED",500005, "Deprecated")
    @JvmField val TIMEOUT         = Failed.Errored   ("TIMEOUT"   ,500006, "Timeout")
    @JvmField val ERRORED         = Failed.Errored   ("ERRORED"   ,500007, "Errored")         // General purpose use
    @JvmField val LIMITED         = Failed.Errored   ("LIMITED"   ,500009, "Limited")

    // Unexpected
    @JvmField val UNEXPECTED      = Failed.Unknown("UNEXPECTED",500008, "Unexpected")

    // Success ( Interactive / Metadata )
    @JvmField val EXIT            = Passed.Succeeded ("EXIT"   , 600002, "Exiting")
    @JvmField val HELP            = Passed.Succeeded ("HELP"   , 600003, "Help")
    @JvmField val ABOUT           = Passed.Succeeded ("ABOUT"  , 600004, "About")
    @JvmField val VERSION         = Passed.Succeeded ("VERSION", 600005, "Version")


    private val mappings = listOf(
        // CRUD
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

        // JOB (States )
        Triple(ACTIVE  .code          , ACTIVE           , 200),
        Triple(INACTIVE.code          , INACTIVE         , 200),
        Triple(STARTING.code          , STARTING         , 200),
        Triple(WAITING .code          , WAITING          , 200),
        Triple(RUNNING .code          , RUNNING          , 200),
        Triple(PAUSED  .code          , PAUSED           , 200),
        Triple(STOPPED .code          , STOPPED          , 200),
        Triple(COMPLETE.code          , COMPLETE         , 200),

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

    @JvmStatic
    fun contains(code:Int):Boolean {
        return lookupCode.contains(code)
    }

    /**
     * Converts a status to a compatible http status code
     * @return Triple with http status code, original code, and optional exception
     */
    @JvmStatic
    fun toHttp(status: Status):Pair<Int, Status> {
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
     * Converts a status to a compatible http status code
     * @return Triple with http status code, original code, and optional exception
     */
    @JvmStatic
    fun toStatus(code:Int): Status? {
        return when(lookupHttp.containsKey(code)){
            true -> {
                val httpCode = lookupHttp[code]
                httpCode?.second
            }
            else -> null
        }
    }


    /**
     * Converts a code to a compatible http status with code
     * @return Triple with http status code, original code, and optional exception
     */
    @JvmStatic
    fun ofCode(code: Int):Status {
        val exists = lookupCode.containsKey(code)
        return when(exists){
            true -> {
                val info = lookupCode[code]
                info?.second ?: BAD_REQUEST
            }
            else -> when {
                code in 1..999 -> Passed.Succeeded(SUCCESS.name, code, SUCCESS.desc)
                code in 2000..2999 -> Failed.Invalid(INVALID.name, code, INVALID.desc)
                code >= 3000 -> Failed.Errored(ERRORED.name, code, ERRORED.desc)
                else -> Failed.Errored(UNEXPECTED.name, code, "Unexpected")
            }
        }
    }
}

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
package kiit.codes

/**
 * Built-in registry of standard [Status] codes covering the most common operation outcomes.
 *
 * Using these is optional — they are provided for convenience and as defaults for kiit-result
 * builder methods. Custom codes can be created by constructing any [Passed] or [Failed] subtype
 * directly.
 *
 * Numeric codes default to HTTP-compatible ranges:
 *   - 200xxx → success / pending
 *   - 400xxx → client / validation failures
 *   - 500xxx → server / unexpected failures
 *   - 600xxx → interactive / metadata
 *
 * HTTP conversion is available via [toHttp]. Each code maps to the closest semantic HTTP status.
 *
 */
object Codes {

    // Success: 200000 + range ( useful for CRUD operations )
    val SUCCESS         = Passed.Succeeded ("SUCCESS", 200001, "Success")
    val CREATED         = Passed.Succeeded ("CREATED", 200002, "Created")
    val UPDATED         = Passed.Succeeded ("UPDATED", 200003, "Updated")
    val FETCHED         = Passed.Succeeded ("FETCHED", 200004, "Fetched")
    val PATCHED         = Passed.Succeeded ("PATCHED", 200005, "Patched")         // E.g. Update a small subset of info
    val DELETED         = Passed.Succeeded ("DELETED", 200006, "Deleted")
    val HANDLED         = Passed.Succeeded ("HANDLED", 200007, "Handled")         // E.g. A silent ok ( similar to http 204 )
    val PENDING         = Passed.Pending   ("PENDING", 200008, "Pending")
    val QUEUED          = Passed.Pending   ("QUEUED" , 200009, "Queued" )
    val CONFIRM         = Passed.Pending   ("CONFIRM", 200010, "Confirm")
    val FILTERED        = Passed.Filtered  ("FILTERED", 200204, "Filtered")         // E.g. Ignored, not exactly an error
    val IGNORED         = Passed.Ignored   ("IGNORED" , 200204, "Ignored")         // E.g. Ignored, not exactly an error

    // Success: 200000 + range ( useful for JOB States )
    val ACTIVE          = Passed.Pending   ("ACTIVE"  , 200101, "Active"  )
    val INACTIVE        = Passed.Pending   ("INACTIVE", 200102, "Inactive")
    val STARTING        = Passed.Pending   ("STARTING", 200103, "Starting")
    val WAITING         = Passed.Pending   ("WAITING" , 200104, "Waiting" )
    val RUNNING         = Passed.Pending   ("RUNNING" , 200105, "Running" )
    val PAUSED          = Passed.Pending   ("PAUSED"  , 200106, "Paused"  )
    val STOPPED         = Passed.Pending   ("STOPPED" , 200107, "Stopped" )
    val COMPLETE        = Passed.Pending   ("COMPLETE", 200108, "Complete")

    // Invalid: 400000 + range
    val BAD_REQUEST     = Failed.Invalid   ("BAD_REQUEST", 400002, "Bad Request")     // E.g. Invalid JSON
    val INVALID         = Failed.Invalid   ("INVALID"    , 400003, "Invalid")         // E.g. Valid   JSON but invalid values
    val NOT_FOUND       = Failed.Invalid   ("NOT_FOUND"  , 400004, "Not found")       // E.g. Resource/End point not found

    // Security related
    val DENIED          = Failed.Denied    ("DENIED"         , 400005, "Denied")          // Presumes a checked condition
    val UNSUPPORTED     = Failed.Denied    ("UNSUPPORTED"    , 400006, "Not supported")   // Presumes a checked condition
    val UNIMPLEMENTED   = Failed.Denied    ("UNIMPLEMENTED"  , 400007, "Not implemented") // Presumes a checked condition
    val UNAVAILABLE     = Failed.Denied    ("UNAVAILABLE"    , 400008, "Not available")   // Presumes a checked condition
    val UNAUTHENTICATED = Failed.Denied    ("UNAUTHENTICATED", 400009, "Unauthenticated") // Presumes a checked condition
    val UNAUTHORIZED    = Failed.Denied    ("UNAUTHORIZED"   , 400010, "Unauthorized")    // Presumes a checked condition

    // Expected errors: 500000 + range
    val MISSING         = Failed.Errored   ("MISSING"   , 500002, "Missing item")    // E.g. Domain model not found
    val FORBIDDEN       = Failed.Errored   ("FORBIDDEN" , 500003, "Forbidden")
    val CONFLICT        = Failed.Errored   ("CONFLICT"  , 500004, "Conflict")
    val DEPRECATED      = Failed.Errored   ("DEPRECATED", 500005, "Deprecated")
    val TIMEOUT         = Failed.Errored   ("TIMEOUT"   , 500006, "Timeout")
    val ERRORED         = Failed.Errored   ("ERRORED"   , 500007, "Errored")         // General purpose use
    val LIMITED         = Failed.Errored   ("LIMITED"   , 500009, "Limited")

    // Unexpected
    val UNEXPECTED      = Failed.Unknown   ("UNEXPECTED", 500008, "Unexpected")

    // Success ( Interactive / Metadata )
    val EXIT            = Passed.Succeeded ("EXIT"   , 600002, "Exiting")
    val HELP            = Passed.Succeeded ("HELP"   , 600003, "Help")
    val ABOUT           = Passed.Succeeded ("ABOUT"  , 600004, "About")
    val VERSION         = Passed.Succeeded ("VERSION", 600005, "Version")

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

        // JOB States
        Triple(ACTIVE  .code         , ACTIVE           , 200),
        Triple(INACTIVE.code         , INACTIVE         , 200),
        Triple(STARTING.code         , STARTING         , 200),
        Triple(WAITING .code         , WAITING          , 200),
        Triple(RUNNING .code         , RUNNING          , 200),
        Triple(PAUSED  .code         , PAUSED           , 200),
        Triple(STOPPED .code         , STOPPED          , 200),
        Triple(COMPLETE.code         , COMPLETE         , 200),

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

    private val lookupByCode = mappings.associateBy { it.first }
    private val lookupByHttp = mappings.associateBy { it.third }

    fun contains(code: Int): Boolean = lookupByHttp.containsKey(code)

    /**
     * Converts a status to a compatible HTTP status code.
     * TODO: HttpCode support to be added when kiit-codes gains an HttpCode dependency.
     */
    fun toHttp(status: Status): Pair<Int, Status> {
        val entry = lookupByCode[status.code]
        return if (entry != null) Pair(entry.third, status) else Pair(status.code, status)
    }

    /**
     * Converts an HTTP status code to a matching [Status], or null if not found.
     */
    fun toStatus(code: Int): Status? = lookupByCode[code]?.second

    /**
     * Converts a numeric code to its matching [Status].
     */
    fun ofCode(code: Int): Status {
        val entry = lookupByHttp[code]
        return when {
            entry != null      -> entry.second
            code in 1..999     -> Passed.Succeeded(SUCCESS.name, code, SUCCESS.message)
            code in 2000..2999 -> Failed.Invalid(INVALID.name, code, INVALID.message)
            code >= 3000       -> Failed.Errored(ERRORED.name, code, ERRORED.message)
            else               -> Failed.Errored(UNEXPECTED.name, code, "Unexpected")
        }
    }
}

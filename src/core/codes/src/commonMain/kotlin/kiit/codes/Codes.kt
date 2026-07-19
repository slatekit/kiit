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
 * Built-in registry of standard [Status] codes covering common operation outcomes.
 *
 * Using these is optional — they're provided as sensible defaults and for kiit-result builder
 * methods. Custom domain codes can be created by constructing any [Passed] or [Failed] subtype
 * directly; only the four categories under each are fixed/closed (see [Status]).
 *
 * Numeric code ranges (conceptual grouping only — see the NOTE on [Status.code]):
 *   200000-200099 Succeeded     200100-200199 Pending
 *   200200-200299 Filtered      200300-200399 Information
 *   400000-400099 Denied        400100-400199 Invalid
 *   500000-500099 Errored       500100-500199 Unserviceable
 *
 * Uniqueness of every code in this registry is enforced at object-init time (see the `init`
 * block below) — a duplicate code will fail loudly the first time [Codes] is touched, rather
 * than silently producing a wrong HTTP mapping.
 */
object Codes {
    // ---- Succeeded (200000-200099) ----
    val SUCCESS = Passed.Succeeded("SUCCESS", 200001, "Success")
    val CREATED = Passed.Succeeded("CREATED", 200002, "Created")
    val UPDATED = Passed.Succeeded("UPDATED", 200003, "Updated")
    val FETCHED = Passed.Succeeded("FETCHED", 200004, "Fetched")
    val PATCHED = Passed.Succeeded("PATCHED", 200005, "Patched")
    val DELETED = Passed.Succeeded("DELETED", 200006, "Deleted")
    val HANDLED = Passed.Succeeded("HANDLED", 200007, "Handled") // e.g. a silent OK, similar to HTTP 204

    // ---- Pending (200100-200199) ----
    val PENDING = Passed.Pending("PENDING", 200101, "Pending")
    val QUEUED = Passed.Pending("QUEUED", 200102, "Queued")
    val CONFIRM = Passed.Pending("CONFIRM", 200103, "Confirm")

    // ---- Filtered (200200-200299) ----
    val SKIPPED = Passed.Filtered("SKIPPED", 200201, "Skipped") // not processed at all
    val DISCARDED = Passed.Filtered("DISCARDED", 200202, "Discarded") // processed, result thrown away

    // ---- Information (200300-200399) ----
    val HELP = Passed.Information("HELP", 200301, "Help")
    val ABOUT = Passed.Information("ABOUT", 200302, "About")
    val VERSION = Passed.Information("VERSION", 200303, "Version")
    val EXIT = Passed.Information("EXIT", 200304, "Exiting")

    // ---- Denied (400000-400099) — security / access-control ----
    val DENIED = Failed.Denied("DENIED", 400001, "Denied")
    val UNAUTHENTICATED = Failed.Denied("UNAUTHENTICATED", 400002, "Unauthenticated")
    val UNAUTHORIZED = Failed.Denied("UNAUTHORIZED", 400003, "Unauthorized")

    // ---- Invalid (400100-400199) — bad input ----
    val BAD_REQUEST = Failed.Invalid("BAD_REQUEST", 400101, "Bad request") // e.g. malformed JSON
    val INVALID = Failed.Invalid("INVALID", 400102, "Invalid") // e.g. well-formed but invalid values
    val NOT_FOUND = Failed.Invalid("NOT_FOUND", 400103, "Not found") // e.g. resource/endpoint not found

    // ---- Errored (500000-500099) — known, expected business-rule failure ----
    val MISSING = Failed.Errored("MISSING", 500001, "Missing item") // e.g. domain model not found
    val FORBIDDEN = Failed.Errored("FORBIDDEN", 500002, "Forbidden")
    val CONFLICT = Failed.Errored("CONFLICT", 500003, "Conflict")
    val DEPRECATED = Failed.Errored("DEPRECATED", 500004, "Deprecated")
    val ERRORED = Failed.Errored("ERRORED", 500005, "Errored") // general purpose use

    // ---- Unserviceable (500100-500199) — valid & permitted, can't be serviced right now ----
    val UNIMPLEMENTED = Failed.Unserviceable("UNIMPLEMENTED", 500101, "Not implemented")
    val UNSUPPORTED = Failed.Unserviceable("UNSUPPORTED", 500102, "Not supported")
    val TIMEOUT = Failed.Unserviceable("TIMEOUT", 500103, "Timeout")
    val RATE_LIMITED = Failed.Unserviceable("RATE_LIMITED", 500104, "Rate limited")
    val UNREACHABLE = Failed.Unserviceable("UNREACHABLE", 500105, "Unreachable") // e.g. dependency down
    val UNDER_MAINTENANCE = Failed.Unserviceable("UNDER_MAINTENANCE", 500106, "Under maintenance")
    val UNEXPECTED = Failed.Unserviceable("UNEXPECTED", 500107, "Unexpected") // unhandled/uncaught path

    /** All built-in codes. Used for reverse lookups — see [CodesToHttp], [CompositeLookup]. */
    val all: List<Status> =
        listOf(
            SUCCESS, CREATED, UPDATED, FETCHED, PATCHED, DELETED, HANDLED,
            PENDING, QUEUED, CONFIRM,
            SKIPPED, DISCARDED,
            HELP, ABOUT, VERSION, EXIT,
            DENIED, UNAUTHENTICATED, UNAUTHORIZED,
            BAD_REQUEST, INVALID, NOT_FOUND,
            MISSING, FORBIDDEN, CONFLICT, DEPRECATED, ERRORED,
            UNIMPLEMENTED, UNSUPPORTED, TIMEOUT, RATE_LIMITED, UNREACHABLE, UNDER_MAINTENANCE, UNEXPECTED,
        )

    private val byCode: Map<Int, Status> = all.associateBy { it.code }

    init {
        check(byCode.size == all.size) {
            val dupes = all.groupBy { it.code }.filterValues { it.size > 1 }.keys
            "Duplicate Status codes detected in Codes registry: $dupes"
        }
    }

    /** Looks up a built-in [Status] by its internal registry code (e.g. 400001). Null if unknown. */
    fun statusForCode(code: Int): Status? = byCode[code]
}

/**
 * Bidirectional conversion between a [Status] and a target protocol's status code (e.g. HTTP).
 *
 * Implementations should be exhaustive over [Status]'s categories ([Passed]/[Failed] subtypes),
 * typically via a `when` with no `else` branch, so a newly added category is caught at compile
 * time. Individual codes within a category do not need an exhaustive mapping — they can be
 * handled via a small overrides table layered on top of the category default (see [CodesToHttp]).
 */
interface CodeLookup {
    /** Converts a [Status] to the target protocol's code. */
    fun toCode(status: Status): Int

    /** Converts a target protocol [code] to a matching [Status], or null if there is no match. */
    fun toStatus(code: Int): Status?
}

/**
 * Default [CodeLookup] implementation mapping [Status] to HTTP status codes.
 *
 * Category -> HTTP default:
 *   Succeeded / Filtered / Information -> 200      Pending -> 202
 *   Denied -> 401      Invalid -> 400      Errored -> 500      Unserviceable -> 503
 *
 * Individual codes can differ from their category's default via [overrides] (e.g. CREATED -> 201,
 * NOT_FOUND -> 404). [toStatus] is derived from [toCode] rather than a separately maintained
 * reverse table, so the two directions can never drift out of sync with each other.
 *
 * Clients needing additional/custom codes should compose with [CompositeLookup] rather than
 * subclassing this type directly — see [CompositeLookup] for why.
 */
open class CodesToHttp(
    private val overrides: Map<Int, Int> = DEFAULT_OVERRIDES,
) : CodeLookup {
    override fun toCode(status: Status): Int {
        overrides[status.code]?.let { return it }
        return when (status) {
            is Passed.Succeeded -> 200
            is Passed.Pending -> 202
            is Passed.Filtered -> 200
            is Passed.Information -> 200
            is Failed.Denied -> 401
            is Failed.Invalid -> 400
            is Failed.Errored -> 500
            is Failed.Unserviceable -> 503
        }
    }

    /**
     * Reverse lookup, derived from [toCode] over the built-in [Codes.all] registry. Note this
     * only finds statuses registered in [Codes] — a caller's own custom [Status] instances that
     * were never added to that registry won't be found here even if they'd resolve to [code]
     * via [toCode]. Use [CompositeLookup] if you need custom statuses to also be reverse-lookupable.
     */
    override fun toStatus(code: Int): Status? = Codes.all.firstOrNull { toCode(it) == code }

    companion object {
        val DEFAULT_OVERRIDES: Map<Int, Int> =
            mapOf(
                Codes.CREATED.code to 201,
                Codes.HANDLED.code to 204,
                Codes.CONFIRM.code to 200,
                Codes.NOT_FOUND.code to 404,
                Codes.MISSING.code to 400,
                Codes.FORBIDDEN.code to 403,
                Codes.CONFLICT.code to 409,
                Codes.DEPRECATED.code to 426,
                Codes.UNIMPLEMENTED.code to 501,
                Codes.UNSUPPORTED.code to 501,
                Codes.TIMEOUT.code to 408,
                Codes.RATE_LIMITED.code to 429,
                Codes.UNEXPECTED.code to 500,
            )
    }
}

/**
 * Composes a [base] [CodeLookup] with client-supplied [extensions], without modifying or
 * subclassing the base implementation (composition over inheritance). [extensions] take
 * precedence over [base] for both directions.
 *
 * [extensions] is keyed by the actual [Status] instance (not just its numeric code) so that
 * [toStatus] can be answered correctly for custom statuses that aren't part of the [Codes.all]
 * registry — a plain `Map<Int, Int>` of code-to-code can't support that, since it never holds
 * a reference to the actual custom Status object to return.
 *
 * ```kotlin
 * val MY_DOMAIN_CODE = Failed.Errored("PAYMENT_DECLINED", 700123, "Payment declined")
 * val lookup = CompositeLookup(CodesToHttp(), mapOf(MY_DOMAIN_CODE to 402))
 * ```
 */
class CompositeLookup(
    private val base: CodeLookup,
    private val extensions: Map<Status, Int>,
) : CodeLookup {
    override fun toCode(status: Status): Int = extensions[status] ?: base.toCode(status)

    override fun toStatus(code: Int): Status? {
        val extended = extensions.entries.firstOrNull { it.value == code }?.key
        return extended ?: base.toStatus(code)
    }
}

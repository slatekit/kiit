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
 * Platform-agnostic status type describing the outcome of any operation — a service call,
 * a background job step, an API request, or a CLI command.
 *
 * Shape (maps directly to JSON / API error responses):
 *   { "name": "TOKEN_EXPIRED", "code": 400009, "message": "Session token expired", "success": false }
 *
 * Hierarchy. Categories are closed/sealed and fixed by design, to enforce a consistent taxonomy
 * across every consumer. Individual codes *within* a category are open — create new domain codes
 * by constructing a [Passed] or [Failed] subtype directly (see [Codes] for the built-in set):
 *
 *   Status  = Passed     | Failed
 *   Passed  = Succeeded  | Pending | Filtered | Information
 *   Failed  = Denied     | Invalid | Errored  | Unserviceable
 *
 * NOTE: [code] ranges are grouped conceptually the way HTTP groups 2xx/4xx/5xx, but this is a
 * conceptual similarity only — [code] is NOT a literal HTTP status. Always convert via a
 * [CodeLookup] implementation such as [CodesToHttp] to obtain a real HTTP status code; never
 * infer one from the numeric prefix.
 */
sealed interface Status {
    /**
     * Unique domain label, e.g. "TOKEN_EXPIRED", "RATE_LIMITED".
     * SCREAMING_SNAKE_CASE, stable — used as a searchable/aggregable key in logs and metrics.
     */
    val name: String

    /**
     * Numeric code. Grouped by category by convention (see [Codes]) but NOT a literal HTTP code.
     */
    val code: Int

    /**
     * Human-readable, constant description — never constructed from runtime data. Per-instance /
     * runtime detail (e.g. "field X was invalid because...") belongs on whatever wraps this
     * Status (an error/result type one layer up), not here — that keeps [message] safe to use
     * as an aggregation key across every occurrence of this status.
     */
    val message: String

    /**
     * True for all [Passed] subtypes, false for all [Failed] subtypes. Callers that don't need
     * to narrow the sealed type can branch on this directly instead of pattern matching.
     */
    val success: Boolean

    /** Returns a copy of this status with an updated [msg], preserving name and code. */
    fun copyMessage(msg: String): Status

    /** Returns a copy of this status with an updated [msg] and [code], preserving name. */
    fun copyAll(msg: String, code: Int): Status

    companion object {
        /**
         * Returns a copy of [defaultStatus] with [msg] and/or [code] overridden, or the original
         * instance unchanged if neither override actually differs from the default. A null or
         * blank [msg] is treated as "no message override" (so it can be safely omitted by callers
         * without extra null-handling).
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Status> ofCode(msg: String?, code: Int?, defaultStatus: T): T {
            val resolvedMsg = msg.takeUnless { it.isNullOrEmpty() } ?: defaultStatus.message
            val resolvedCode = code ?: defaultStatus.code

            return if (resolvedMsg == defaultStatus.message && resolvedCode == defaultStatus.code) {
                defaultStatus
            } else {
                defaultStatus.copyAll(resolvedMsg, resolvedCode) as T
            }
        }

        /**
         * Resolves a status from an optional [msg] override and an optional [rawStatus] override,
         * falling back to [status] when neither is supplied. [rawStatus], if present, is used as
         * the base instead of [status]; [msg], if present, is then applied on top of that base.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Status> ofStatus(msg: String?, rawStatus: T?, status: T): T {
            val base = rawStatus ?: status
            return if (msg == null) base else base.copyMessage(msg) as T
        }

        /** Returns the lowercase category discriminant for a status, e.g. "denied", "errored". */
        fun toType(status: Status): String =
            when (status) {
                is Passed.Succeeded -> "succeeded"
                is Passed.Pending -> "pending"
                is Passed.Filtered -> "filtered"
                is Passed.Information -> "information"
                is Failed.Denied -> "denied"
                is Failed.Invalid -> "invalid"
                is Failed.Errored -> "errored"
                is Failed.Unserviceable -> "unserviceable"
            }
    }
}

/**
 * Parent sealed type for all non-failure statuses (success = true for every subtype).
 * Subtypes: [Succeeded], [Pending], [Filtered], [Information].
 */
sealed class Passed : Status {
    final override val success: Boolean get() = true

    /** Operation's primary purpose completed (e.g. a value was created, fetched, updated). */
    data class Succeeded(override val name: String, override val code: Int, override val message: String) : Passed()

    /** Operation accepted but not yet fully processed (e.g. queued, waiting, confirmed). */
    data class Pending(override val name: String, override val code: Int, override val message: String) : Passed()

    /**
     * Item was excluded from the operation's normal output. Covers both:
     *   - not processed at all (e.g. SKIPPED — screened out before any work happened), and
     *   - processed, then its result was deliberately discarded (e.g. DISCARDED).
     * The distinction is carried by [name]/[code], not by separate types — see [Codes.SKIPPED]
     * and [Codes.DISCARDED].
     */
    data class Filtered(override val name: String, override val code: Int, override val message: String) : Passed()

    /**
     * Informational / metadata response — no primary operation was performed.
     * E.g. HELP, ABOUT, VERSION output from a CLI command.
     */
    data class Information(override val name: String, override val code: Int, override val message: String) : Passed()

    override fun copyAll(msg: String, code: Int): Status =
        when (this) {
            is Succeeded -> copy(code = code, message = msg)
            is Pending -> copy(code = code, message = msg)
            is Filtered -> copy(code = code, message = msg)
            is Information -> copy(code = code, message = msg)
        }

    override fun copyMessage(msg: String): Status =
        when (this) {
            is Succeeded -> copy(message = msg)
            is Pending -> copy(message = msg)
            is Filtered -> copy(message = msg)
            is Information -> copy(message = msg)
        }
}

/**
 * Parent sealed type for all failure statuses (success = false for every subtype).
 * Subtypes: [Denied], [Invalid], [Errored], [Unserviceable].
 */
sealed class Failed : Status {
    final override val success: Boolean get() = false

    /** Security / access-control failure — the caller is not permitted to perform this action. */
    data class Denied(override val name: String, override val code: Int, override val message: String) : Failed()

    /** The request as given cannot be satisfied — malformed input, invalid values, or not found. */
    data class Invalid(override val name: String, override val code: Int, override val message: String) : Failed()

    /** A known, expected business-rule failure — understood and handled by the caller. */
    data class Errored(override val name: String, override val code: Int, override val message: String) : Failed()

    /**
     * The request is valid and permitted, but cannot be serviced right now for reasons unrelated
     * to what was sent — capacity, timeout, an unimplemented/unsupported capability, planned
     * maintenance, or a genuinely unexpected/unhandled failure (see [Codes.UNEXPECTED]).
     */
    data class Unserviceable(override val name: String, override val code: Int, override val message: String) : Failed()

    override fun copyAll(msg: String, code: Int): Status =
        when (this) {
            is Denied -> copy(code = code, message = msg)
            is Invalid -> copy(code = code, message = msg)
            is Errored -> copy(code = code, message = msg)
            is Unserviceable -> copy(code = code, message = msg)
        }

    override fun copyMessage(msg: String): Status =
        when (this) {
            is Denied -> copy(message = msg)
            is Invalid -> copy(message = msg)
            is Errored -> copy(message = msg)
            is Unserviceable -> copy(message = msg)
        }
}

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
 * Platform-agnostic status type describing the outcome of any operation.
 *
 * Shape (maps directly to JSON / API error responses):
 *   { "name": "TOKEN_EXPIRED", "code": 400009, "message": "Session token expired", "success": false }
 *
 * Hierarchy:
 *   Status  = Passed  | Failed
 *   Passed  = Succeeded | Pending | Filtered | Ignored
 *   Failed  = Denied    | Invalid | Errored  | Unknown
 */
interface Status {
    /**
     * Unique domain label, e.g. "TOKEN_EXPIRED", "RATE_LIMITED".
     * Should be SCREAMING_SNAKE_CASE and stable — used as a searchable key in logs.
     */
    val name: String

    /**
     * Numeric code whose defaults align with HTTP status codes (200, 400, 500 ranges).
     * Flexible for non-HTTP runtimes — convert via [Codes.toHttp].
     */
    val code: Int

    /**
     * Human-readable description of this status.
     * Must be a constant — never constructed from runtime data.
     */
    val message: String

    /**
     * Boolean shortcut — true for [Passed.Succeeded] and [Passed.Pending],
     * false for [Passed.Filtered], [Passed.Ignored], and all [Failed] subtypes.
     * Callers that don't need to narrow the sealed type can use this directly.
     */
    val success: Boolean

    fun copyMessage(msg: String): Status

    fun copyAll(msg: String, code: Int): Status

    companion object {
        /**
         * Returns a copy of [defaultStatus] with an updated [msg] and/or [code].
         * Optimised to return the original instance when nothing would change.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Status> ofCode(msg: String?, code: Int?, defaultStatus: T): T {
            if (code == null && msg == null || msg == "") return defaultStatus
            if (code == defaultStatus.code && msg == null) return defaultStatus
            if (code == defaultStatus.code && msg == defaultStatus.message) return defaultStatus
            return defaultStatus.copyAll(msg ?: defaultStatus.message, code ?: defaultStatus.code) as T
        }

        /**
         * Returns a copy of [status] with an updated [msg] and/or the override from [rawStatus].
         * Optimised to return the original instance when nothing would change.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Status> ofStatus(msg: String?, rawStatus: T?, status: T): T {
            if (msg == null && rawStatus == null) return status
            if (msg == null && rawStatus != null) return rawStatus
            if (msg != null && rawStatus == null) return status.copyMessage(msg) as T
            if (msg != null && rawStatus != null) return rawStatus.copyMessage(msg) as T
            return status
        }

        fun toType(status: Status): String {
            return when (status) {
                is Passed.Succeeded -> "succeeded"
                is Passed.Pending -> "pending"
                is Passed.Filtered -> "filtered"
                is Passed.Ignored -> "ignored"
                is Failed.Denied -> "denied"
                is Failed.Invalid -> "invalid"
                is Failed.Errored -> "errored"
                is Failed.Unknown -> "unknown"
                else -> "unknown"
            }
        }
    }
}

/**
 * Parent sealed type for all non-failure statuses.
 * Subtypes: [Succeeded], [Pending], [Filtered], [Ignored].
 */
sealed class Passed : Status {
    /** Operation completed successfully. */
    data class Succeeded(override val name: String, override val code: Int, override val message: String) : Passed() {
        override val success = true
    }

    /** Operation accepted but not yet fully processed (e.g. queued, waiting). */
    data class Pending(override val name: String, override val code: Int, override val message: String) : Passed() {
        override val success = true
    }

    /** Item was intentionally excluded from processing (e.g. deduplicated, out of scope). */
    data class Filtered(override val name: String, override val code: Int, override val message: String) : Passed() {
        override val success = true
    }

    /** Item was processed but its result was deliberately discarded or suppressed. */
    data class Ignored(override val name: String, override val code: Int, override val message: String) : Passed() {
        override val success = true
    }

    override fun copyAll(msg: String, code: Int): Status {
        return when (this) {
            is Succeeded -> this.copy(code = code, message = msg)
            is Pending -> this.copy(code = code, message = msg)
            is Filtered -> this.copy(code = code, message = msg)
            is Ignored -> this.copy(code = code, message = msg)
        }
    }

    override fun copyMessage(msg: String): Status {
        return when (this) {
            is Succeeded -> this.copy(message = msg)
            is Pending -> this.copy(message = msg)
            is Filtered -> this.copy(message = msg)
            is Ignored -> this.copy(message = msg)
        }
    }
}

/**
 * Parent sealed type for all failure statuses (success = false for all subtypes).
 * Subtypes: [Denied], [Invalid], [Errored], [Unknown].
 */
sealed class Failed : Status {
    /** Security / access-control failure — the caller is not permitted to perform this action. */
    data class Denied(override val name: String, override val code: Int, override val message: String) : Failed() {
        override val success = false
    }

    /** The input data is malformed or fails validation rules. */
    data class Invalid(override val name: String, override val code: Int, override val message: String) : Failed() {
        override val success = false
    }

    /** A known business-rule failure — expected, handled, and recoverable. */
    data class Errored(override val name: String, override val code: Int, override val message: String) : Failed() {
        override val success = false
    }

    /** An unexpected or unhandled failure — equivalent to an uncaught exception path. */
    data class Unknown(override val name: String, override val code: Int, override val message: String) : Failed() {
        override val success = false
    }

    override fun copyAll(msg: String, code: Int): Status {
        return when (this) {
            is Denied -> this.copy(name = name, code = code, message = msg)
            is Invalid -> this.copy(name = name, code = code, message = msg)
            is Errored -> this.copy(name = name, code = code, message = msg)
            is Unknown -> this.copy(name = name, code = code, message = msg)
        }
    }

    override fun copyMessage(msg: String): Status {
        return when (this) {
            is Denied -> this.copy(message = msg)
            is Invalid -> this.copy(message = msg)
            is Errored -> this.copy(message = msg)
            is Unknown -> this.copy(message = msg)
        }
    }
}

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

package slatekit.results

/**
 * Interface to represent a Status Code with both an integer and descriptive message
 * @sample :
 * { code: 8000, msg: "Invalid request" }
 * { code: 8001, msg: "Unauthorized"    }
 *
 */
sealed class Status {
    abstract val code: Int
    abstract val msg: String

    /**
     * Default implementations of status codes with logical groups of [Status]
     *
     * 1. [Succeeded] : Used for any successful codes/scenarios
     * 2. [Pending]   : Success group for pending/queued results
     * 2. [Invalid]   : Err group to represent a bad request / inputs
     * 4. [Ignored]   : Err group to represent ignored / filtered cases
     * 3. [Denied]    : Err group to represent denied requests
     * 5. [Errored]   : Err group to represent any error/failure scenarios known at compile time
     * 6. [Unexpected] : Err group to represent any unhandled exceptions
     */
    data class Succeeded(override val code: Int, override val msg: String) : Status()

    data class Pending(override val code: Int, override val msg: String) : Status()
    data class Denied(override val code: Int, override val msg: String) : Status()
    data class Ignored(override val code: Int, override val msg: String) : Status()
    data class Invalid(override val code: Int, override val msg: String) : Status()
    data class Errored(override val code: Int, override val msg: String) : Status()
    data class Unexpected(override val code: Int, override val msg: String) : Status()

    fun copyAll(msg: String, code: Int): Status {
        return when (this) {
            is Succeeded -> this.copy(code = code, msg = msg)
            is Pending -> this.copy(code = code, msg = msg)
            is Denied -> this.copy(code = code, msg = msg)
            is Invalid -> this.copy(code = code, msg = msg)
            is Ignored -> this.copy(code = code, msg = msg)
            is Errored -> this.copy(code = code, msg = msg)
            is Unexpected -> this.copy(code = code, msg = msg)
        }
    }

    fun copyMsg(msg: String): Status {
        return when (this) {
            is Succeeded -> this.copy(msg = msg)
            is Pending -> this.copy(msg = msg)
            is Denied -> this.copy(msg = msg)
            is Invalid -> this.copy(msg = msg)
            is Ignored -> this.copy(msg = msg)
            is Errored -> this.copy(msg = msg)
            is Unexpected -> this.copy(msg = msg)
        }
    }

    companion object {

        @JvmStatic
        fun ofCode(msg: String?, code: Int?, defaultStatus: Status): Status {
            // NOTE: There is small optimization here to avoid creating a new instance
            // of [Status] if the msg/code are empty and or they are the same as Success.
            if (code == null && msg == null || msg == "") return defaultStatus
            if (code == defaultStatus.code && msg == null) return defaultStatus
            if (code == defaultStatus.code && msg == defaultStatus.msg) return defaultStatus
            return defaultStatus.copyAll(msg ?: defaultStatus.msg, code ?: defaultStatus.code)
        }

        @JvmStatic
        fun <T:Status> ofStatus(msg: String?, rawStatus: T?, status: T): T {
            // NOTE: There is small optimization here to avoid creating a new instance
            // of [Status] if the msg/code are empty and or they are the same as Success.
            if (msg == null && rawStatus == null) return status
            if (msg == null && rawStatus != null) return rawStatus
            if (msg != null && rawStatus == null) return status.copyMsg(msg) as T
            if (msg != null && rawStatus != null) return rawStatus.copyMsg(msg) as T
            return status
        }
    }
}

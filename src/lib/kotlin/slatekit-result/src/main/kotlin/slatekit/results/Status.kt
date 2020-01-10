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
 * Interface to represent a Status with both an integer code and descriptive message
 * @sample :
 * { code: 8000, msg: "Invalid request" }
 * { code: 8001, msg: "Unauthorized"    }
 *
 */
interface Status {
    val code:Int
    val msg: String

    fun copyMsg(msg: String): Status
    fun copyAll(msg: String, code: Int): Status

    companion object {

        /**
         * Minor optimization to avoid unnecessary copying of Status
         */
        fun <T : Status> ofCode(msg: String?, code: Int?, defaultStatus: T): T {
            // NOTE: There is small optimization here to avoid creating a new instance
            // of [Status] if the msg/code are empty and or they are the same as Success.
            if (code == null && msg == null || msg == "") return defaultStatus
            if (code == defaultStatus.code && msg == null) return defaultStatus
            if (code == defaultStatus.code && msg == defaultStatus.msg) return defaultStatus
            return defaultStatus.copyAll(msg ?: defaultStatus.msg, code ?: defaultStatus.code) as T
        }

        /**
         * Minor optimization to avoid unnecessary copying of Status
         */
        fun <T : Status> ofStatus(msg: String?, rawStatus: T?, status: T): T {
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


/**
 * Sum Type to represent the different possible Statuses that can be supplied to the @see[Success]
 */
sealed class Passed : Status {
    data class Succeeded (override val code: Int, override val msg: String) : Passed()
    data class Pending   (override val code: Int, override val msg: String) : Passed()

    override fun copyAll(msg: String, code: Int): Status {
        return when (this) {
            is Succeeded -> this.copy(code = code, msg = msg)
            is Pending -> this.copy(code = code, msg = msg)
        }
    }

    override fun copyMsg(msg: String): Status {
        return when (this) {
            is Succeeded -> this.copy(msg = msg)
            is Pending -> this.copy(msg = msg)
        }
    }
}


/**
 * Sum Type to represent the different possible Statuses that can be supplied to the @see[Failure]
 */
sealed class Failed : Status {
    data class Denied    (override val code: Int, override val msg: String) : Failed()
    data class Ignored   (override val code: Int, override val msg: String) : Failed()
    data class Invalid   (override val code: Int, override val msg: String) : Failed()
    data class Errored   (override val code: Int, override val msg: String) : Failed()
    data class Unexpected(override val code: Int, override val msg: String) : Failed()

    override fun copyAll(msg: String, code: Int): Status {
        return when (this) {
            is Denied  -> this.copy(code = code, msg = msg)
            is Invalid -> this.copy(code = code, msg = msg)
            is Ignored -> this.copy(code = code, msg = msg)
            is Errored -> this.copy(code = code, msg = msg)
            is Unexpected -> this.copy(code = code, msg = msg)
        }
    }

    override fun copyMsg(msg: String): Status {
        return when (this) {
            is Denied -> this.copy(msg = msg)
            is Invalid -> this.copy(msg = msg)
            is Ignored -> this.copy(msg = msg)
            is Errored -> this.copy(msg = msg)
            is Unexpected -> this.copy(msg = msg)
        }
    }
}




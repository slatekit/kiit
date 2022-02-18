package slatekit.status

/**
 * Interface to represent a Status with both an integer code and description
 * @sample :
 * { name: "INVALID"     , code: 400000, msg: "Invalid request" }
 * { name: "UNAUTHORIZED", code: 400001, msg: "Unauthorized"    }
 *
 */
interface Status {
    /**
     * Used as short user-friendly enum e.g. "INVALID", "UNAUTHORIZED"
     */
    val name:String

    /**
     * Used as a generic application code that can be converted to other codes such as HTTP.
     */
    val code:Int

    /**
     * Description for status
     */
    val desc: String

    /**
     * Represents success or failure
     */
    val success:Boolean

    fun copyDesc(msg: String): Status
    fun copyAll(msg: String, code: Int): Status

    companion object {

        /**
         * Minor optimization to avoid unnecessary copying of Status
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Status> ofCode(msg: String?, code: Int?, defaultStatus: T): T {
            // NOTE: There is small optimization here to avoid creating a new instance
            // of [Status] if the msg/code are empty and or they are the same as Success.
            if (code == null && msg == null || msg == "") return defaultStatus
            if (code == defaultStatus.code && msg == null) return defaultStatus
            if (code == defaultStatus.code && msg == defaultStatus.desc) return defaultStatus
            return defaultStatus.copyAll(msg ?: defaultStatus.desc, code ?: defaultStatus.code) as T
        }

        /**
         * Minor optimization to avoid unnecessary copying of Status
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Status> ofStatus(msg: String?, rawStatus: T?, status: T): T {
            // NOTE: There is small optimization here to avoid creating a new instance
            // of [Status] if the msg/code are empty and or they are the same as Success.
            if (msg == null && rawStatus == null) return status
            if (msg == null && rawStatus != null) return rawStatus
            if (msg != null && rawStatus == null) return status.copyDesc(msg) as T
            if (msg != null && rawStatus != null) return rawStatus.copyDesc(msg) as T
            return status
        }

        fun toType(status:Status):String {
            val name:String = when(status) {
                is Passed.Succeeded -> "Succeeded"
                is Passed.Pending   -> "Pending"
                is Failed.Denied    -> "Denied"
                is Failed.Ignored   -> "Ignored"
                is Failed.Invalid   -> "Invalid"
                is Failed.Errored   -> "Errored"
                is Failed.Unknown   -> "Unknown"
                else -> Failed::Unknown.name
            }
            return name
        }
    }
}


/**
 * Sum Type to represent the different possible Statuses that can be supplied to the @see[Success]
 */
sealed class Passed : Status {
    data class Succeeded (override val name:String, override val code: Int, override val desc: String) : Passed() { override val success = true }
    data class Pending   (override val name:String, override val code: Int, override val desc: String) : Passed() { override val success = true }

    override fun copyAll(msg: String, code: Int): Status {
        return when (this) {
            is Succeeded -> this.copy(code = code, desc = msg)
            is Pending -> this.copy(code = code, desc = msg)
        }
    }

    override fun copyDesc(msg: String): Status {
        return when (this) {
            is Succeeded -> this.copy(desc = msg)
            is Pending -> this.copy(desc = msg)
        }
    }
}


/**
 * Sum Type to represent the different possible Statuses that can be supplied to the @see[Failure]
 */
sealed class Failed : Status {
    data class Denied    (override val name:String, override val code: Int, override val desc: String) : Failed() { override val success = false }// Security related
    data class Ignored   (override val name:String, override val code: Int, override val desc: String) : Failed() { override val success = false }// Ignored for processing
    data class Invalid   (override val name:String, override val code: Int, override val desc: String) : Failed() { override val success = false }// Bad inputs
    data class Errored   (override val name:String, override val code: Int, override val desc: String) : Failed() { override val success = false }// Expected failures
    data class Unknown   (override val name:String, override val code: Int, override val desc: String) : Failed() { override val success = false }// Unexpected failures


    override fun copyAll(msg: String, code: Int): Status {
        return when (this) {
            is Denied  -> this.copy(name = name, code = code, desc = msg)
            is Invalid -> this.copy(name = name, code = code, desc = msg)
            is Ignored -> this.copy(name = name, code = code, desc = msg)
            is Errored -> this.copy(name = name, code = code, desc = msg)
            is Unknown -> this.copy(name = name, code = code, desc = msg)
        }
    }

    override fun copyDesc(msg: String): Status {
        return when (this) {
            is Denied  -> this.copy(desc = msg)
            is Invalid -> this.copy(desc = msg)
            is Ignored -> this.copy(desc = msg)
            is Errored -> this.copy(desc = msg)
            is Unknown -> this.copy(desc = msg)
        }
    }
}
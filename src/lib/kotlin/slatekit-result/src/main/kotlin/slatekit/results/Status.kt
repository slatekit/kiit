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
            return when(status) {
                is Passed.Succeeded -> Passed::Succeeded.name
                is Passed.Pending -> Passed::Pending.name
                is Failed.Denied  -> Failed::Denied .name
                is Failed.Ignored -> Failed::Ignored.name
                is Failed.Invalid -> Failed::Invalid.name
                is Failed.Errored -> Failed::Errored.name
                is Failed.Unknown -> Failed::Unknown.name
                else -> Failed::Unknown.name
            }
        }
    }
}


/**
 * Sum Type to represent the different possible Statuses that can be supplied to the @see[Success]
 */
sealed class Passed : Status {
    data class Succeeded (override val name:String, override val code: Int, override val desc: String) : Passed()
    data class Pending   (override val name:String, override val code: Int, override val desc: String) : Passed()

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
    data class Denied    (override val name:String, override val code: Int, override val desc: String) : Failed() // Security related
    data class Ignored   (override val name:String, override val code: Int, override val desc: String) : Failed() // Ignored for processing
    data class Invalid   (override val name:String, override val code: Int, override val desc: String) : Failed() // Bad inputs
    data class Errored   (override val name:String, override val code: Int, override val desc: String) : Failed() // Expected failures
    data class Unknown   (override val name:String, override val code: Int, override val desc: String) : Failed() // Unexpected failures


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




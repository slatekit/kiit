package slatekit.results


/**
 * These exceptions correspond 1 to 1 to the logical groups of Status
 * e.g. see [Status]:
 *   data class Pending
 *   data class Denied
 *   data class Ignored
 *   data class Invalid
 *   data class Errored
 */
//data class PendingException(val msg:String?, val status:Status? = null, val origin:Throwable? = null) : Exception(msg, origin)
interface StatusException {
    val msg:String?
    val status:Status?
    val origin:Throwable?
}


data class DeniedException (override val msg:String?,
                            override val status:Status? = null,
                            override val origin:Throwable? = null) : Exception(msg, origin), StatusException

data class InvalidException(override val msg:String?,
                            override val status:Status? = null,
                            override val origin:Throwable? = null) : Exception(msg, origin), StatusException

data class IgnoredException(override val msg:String?,
                            override val status:Status? = null,
                            override val origin:Throwable? = null) : Exception(msg, origin), StatusException

data class ErroredException(override val msg:String?,
                            override val status:Status? = null,
                            override val origin:Throwable? = null) : Exception(msg, origin), StatusException

data class UnexpectedException(override val msg:String?,
                               override val status:Status? = null,
                               override val origin:Throwable? = null) : Exception(msg, origin), StatusException
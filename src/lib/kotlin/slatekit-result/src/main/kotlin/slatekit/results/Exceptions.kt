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
data class DeniedException (val msg:String?, val status:Status? = null, val origin:Throwable? = null) : Exception(msg, origin)
data class IgnoredException(val msg:String?, val status:Status? = null, val origin:Throwable? = null) : Exception(msg, origin)
data class InvalidException(val msg:String?, val status:Status? = null, val origin:Throwable? = null) : Exception(msg, origin)
data class ErroredException(val msg:String?, val status:Status? = null, val origin:Throwable? = null) : Exception(msg, origin)
data class UnexpectedException(val msg:String?, val status:Status? = null, val origin:Throwable? = null) : Exception(msg, origin)
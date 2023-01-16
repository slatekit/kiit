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

package kiit.results

/**
 * These exceptions correspond 1 to 1 to the logical groups of Status
 * e.g. see [Status]:
 *   data class Pending
 *   data class Denied
 *   data class Ignored
 *   data class Invalid
 *   data class Errored
 */
// data class PendingException(val msg:String?, val status:Status? = null, val origin:Throwable? = null) : Exception(msg, origin)
interface StatusException {
    val msg: String?
    val status: Status?
    val origin: Throwable?
}

data class DeniedException(
    override val msg: String?,
    override val status: Failed.Denied? = null,
    override val origin: Throwable? = null
) : Exception(msg, origin), StatusException

data class InvalidException(
    override val msg: String?,
    override val status: Failed.Invalid? = null,
    override val origin: Throwable? = null
) : Exception(msg, origin), StatusException

data class IgnoredException(
    override val msg: String?,
    override val status: Failed.Ignored? = null,
    override val origin: Throwable? = null
) : Exception(msg, origin), StatusException

data class ErroredException(
    override val msg: String?,
    override val status: Failed.Errored? = null,
    override val origin: Throwable? = null
) : Exception(msg, origin), StatusException

data class UnexpectedException(
    override val msg: String?,
    override val status: Failed.Unknown? = null,
    override val origin: Throwable? = null
) : Exception(msg, origin), StatusException

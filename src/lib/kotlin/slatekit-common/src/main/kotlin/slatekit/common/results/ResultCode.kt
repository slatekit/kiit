/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.results

/**
 * minimal subset of http status codes that are general purpose enough for anything
 */

object ResultCode {
    const val SUCCESS = 200
    const val CONFIRM = 230

    const val FAILURE = 400
    const val BAD_REQUEST = 400
    const val UNAUTHORIZED = 401
    const val NOT_FOUND = 404
    const val CONFLICT = 409
    const val DEPRECATED = 426

    const val UNEXPECTED_ERROR = 500
    const val NOT_IMPLEMENTED = 501
    const val NOT_AVAILABLE = 503

    const val HELP = 1000
    const val MISSING = 1404
    const val EXIT = 1001
    const val VERSION = 1002
}

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


val SUCCESS = 200
val CONFIRM = 230

val FAILURE = 400
val BAD_REQUEST = 400
val UNAUTHORIZED = 401
val NOT_FOUND = 404
val CONFLICT = 409
val DEPRECATED = 426

val UNEXPECTED_ERROR = 500
val NOT_IMPLEMENTED = 501
val NOT_AVAILABLE = 503

val HELP = 1000
val EXIT = 1001
val VERSION = 1002
val MISSING = 1404

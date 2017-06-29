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

package slatekit.common.http

/**
 * Http exception for status
 * @param code   : status code
 * @param msg    : error message
 * @param tag    : optional tag for client reference
 */
data class HttpException(
        val code: Int,
        val msg: String,
        val tag: String = ""
)
    : RuntimeException(code.toString() + " Error: " + msg)

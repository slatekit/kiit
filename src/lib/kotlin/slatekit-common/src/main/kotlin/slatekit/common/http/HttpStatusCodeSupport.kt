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


interface HttpStatusCodeSupport {

    fun statusCode(): Int


    val isOk: Boolean get() = isCode(200)


    val isSuccess: Boolean get() = is2xx


    val isError: Boolean get() = is4xx || is5xx


    val isNotError: Boolean get() = !isError


    val isRedirect: Boolean get() = is3xx


    val isClientError: Boolean get() = is4xx


    val isServerError: Boolean get() = is5xx


    val is2xx: Boolean get() = isInRange(200, 299)


    val is3xx: Boolean get() = isInRange(300, 399)


    val is4xx: Boolean get() = isInRange(400, 499)


    val is5xx: Boolean get() = isInRange(500, 599)


    /**
     * tests if the code is between the lower and upper bound (inclusive)
     * @param code
     * @return
     */
    fun isCode(code: Int): Boolean = statusCode() == code


    /**
     * tests if the code is between the lower and upper bound (inclusive)
     * @param lower
     * @param upper
     * @return
     */
    fun isInRange(lower: Int, upper: Int): Boolean = statusCode() in lower..upper
}

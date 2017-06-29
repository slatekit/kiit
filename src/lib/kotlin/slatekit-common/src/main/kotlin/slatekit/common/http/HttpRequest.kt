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
 * Represents an http request with all the parameters/options
 * @param url            : Url of the request
 * @param method         : Method ( GET/POST/etc)
 * @param params         : parameters ( url parameters for get, form parameters for post )
 * @param headers        : http headers
 * @param connectTimeOut : timeout of operation
 * @param readTimeOut    : timeout for read operation
 */
data class HttpRequest(
        val url: String,
        val method: HttpMethod,
        val params: List<Pair<String, String>>?,
        val headers: List<Pair<String, String>>?,
        val credentials: HttpCredentials? = null,
        val entity: String?,
        val connectTimeOut: Int,
        val readTimeOut: Int
) {
}
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
 * Represents an http response with status code, headers, and an optionally
 * ( already parsed / extracted ) result.
 * @param code : http status code
 * @param headers : http headers
 * @param result : already parsed result of the response ( slate library checks for some
 *                  commonly values ( e.g. json result, and also slate.operationResult )
 */
data class HttpResponse(
    val code: Int,
    val headers: Map<String, List<String>>,
    val result: Any?
)
    : HttpStatusCodeSupport {

    val empty = listOf<String>()

    /**
     * Get the header value for the supplied header name
     * @param key : name of header
     * @return
     */
    fun header(key: String): String? = headers[key]?.let { i -> i[0] }

    /**
     * Gets all of the multiple headers for the supplied key
     * @param key : name of header
     * @return
     */
    fun headerSeq(key: String): List<String> = headers.getOrElse(key, { empty })

    /** The full status line. like "HTTP/1.1 200 OK"
     * throws a RuntimeException if "Status" is not in headers
     */
    fun status(): String = header("Status") ?: ""

    /**
     * Gets the http status code.
     * @return
     */
    override fun statusCode(): Int = code

    /**
     * Gets the Content-Type header value
     * @return
     */
    val contentType: String? get() = header("Content-Type")
}

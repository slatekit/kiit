/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.common.requests

import slatekit.common.Failure
import slatekit.common.Result
import slatekit.common.Success

/**
 * General purpose class to model a Response at an application boundary ( such as http response )
 * NOTE: This is used for the APIs in Slate Kit
 * @param success : Whether or not the response is successful
 * @param code : A status code ( can be the http status code )
 * @param meta : Meta data for the response ( can be used for headers for http )
 * @param value : The actual value returned by the response
 * @param msg : Message in the case of an failure
 * @param err : Exception in event of failure
 * @param tag : Tag used as a correlation field
 */
data class Response<out T>(
    val success: Boolean,
    val code: Int,
    val meta: Map<String, String>?,
    val value: T?,
    val msg: String? = null,
    val err: Exception? = null,
    val tag: String? = null
) {

    /**
     * adds to the existing metadata
     */
    fun withMeta(meta: List<Pair<String, String>>): Response<T> {
        return this.meta?.let {
            copy(meta = it.plus(meta))
        } ?: copy(meta = meta.toMap())
    }
}

/**
 * Converts result to Response.
 */
fun <T, E> Result<T, E>.toResponse(): Response<T> {
    return when (this) {
        is Success -> Response(this.success, this.code, null, this.data, this.msg, null)
        is Failure -> {
            val ex = when (this.err) {
                is Exception -> this.err
                else -> Exception(this.err.toString())
            }
            Response(this.success, this.code, null, null, this.msg, ex)
        }
    }
}

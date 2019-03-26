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

import slatekit.results.StatusCodes


/**
 * General purpose class to model a Response at an application boundary ( such as http response )
 * NOTE: This is used for the APIs in Slate Kit
 */
interface Response<out T>  {
    val success: Boolean
    val code: Int
    val meta: Map<String, String>?
    val value: T?
    val msg: String?
    val err: Exception?
    val tag: String?

    /**
     * adds to the existing metadata
     */
    fun withMeta(meta: List<Pair<String, String>>): Response<T>
}


fun <T> Response<T>.isInSuccessRange(): Boolean = this.code in StatusCodes.SUCCESS.code .. StatusCodes.QUEUED.code
fun <T> Response<T>.isFilteredOut(): Boolean = this.code == StatusCodes.IGNORED.code
fun <T> Response<T>.isInBadRequestRange(): Boolean = this.code in StatusCodes.BAD_REQUEST.code .. StatusCodes.UNAUTHORIZED.code
fun <T> Response<T>.isInFailureRange(): Boolean = this.code in StatusCodes.ERRORED.code .. StatusCodes.UNEXPECTED.code
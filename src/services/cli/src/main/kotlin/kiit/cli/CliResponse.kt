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
package kiit.cli

import kiit.common.args.Args
import kiit.requests.Response
import kiit.results.Codes
import kiit.results.Passed

/**
 * General purpose class to model a Response at an application boundary ( such as http response )
 * NOTE: This is used for the APIs in Slate Kit
 * @param success : Whether or not the response is successful
 * @param code : A status code ( can be the http status code )
 * @param meta : Meta data for the response ( can be used for headers for http )
 * @param value : The actual value returned by the response
 * @param desc : Message in the case of an failure
 * @param err : Exception in event of failure
 * @param tag : Tag used as a correlation field
 */
data class CliResponse<out T>(
        val request: CliRequest,
        override val success: Boolean,
        override val name: String,
        override val type: String,
        override val code: Int,
        override val meta: Map<String, String>?,
        override val value: T?,
        override val desc: String? = null,
        override val err: Exception? = null,
        override val tag: String? = null
) : Response<T> {

    /**
     * adds to the existing metadata
     */
    override fun withMeta(meta: List<Pair<String, String>>): Response<T> {
        return this.meta?.let {
            copy(meta = it.plus(meta))
        } ?: copy(meta = meta.toMap())
    }

    companion object {
        val empty = CliResponse(
                CliRequest.build(Args.empty(), ""),
                true,
                Codes.SUCCESS.name,
                Passed::Succeeded.name,
                1,
                mapOf(),
                "empty"
        )
    }
}

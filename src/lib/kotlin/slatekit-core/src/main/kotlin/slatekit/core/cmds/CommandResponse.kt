package slatekit.core.cmds

import slatekit.common.args.Args
import slatekit.common.requests.Response
import slatekit.common.toResponse
import slatekit.results.Try

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
data class CommandResponse<out T>(
        val request: CommandRequest,
        override val success: Boolean,
        override val code: Int,
        override val meta: Map<String, String>?,
        override val value: T?,
        override val msg: String? = null,
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

        operator fun invoke(request: CommandRequest, result: Try<*>): CommandResponse<Any> {
            val response = result.toResponse()
            return CommandResponse(
                    request,
                    response.success,
                    response.code,
                    response.meta,
                    response.value,
                    response.msg,
                    response.err,
                    response.tag
            )
        }

        val empty = CommandResponse(
                CommandRequest.build(Args.default()),
                true,
                1,
                mapOf(),
                "empty"
        )
    }
}
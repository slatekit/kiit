package slatekit.common.requests

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
data class CommonResponse<out T>(
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

    override fun withMeta(meta: List<Pair<String, String>>): Response<T> {
        return this.meta?.let {
            copy(meta = it.plus(meta))
        } ?: copy(meta = meta.toMap())
    }
}
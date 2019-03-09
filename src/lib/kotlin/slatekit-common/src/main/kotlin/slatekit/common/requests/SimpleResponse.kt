package slatekit.common.requests

data class SimpleResponse<out T>(
        override val success: Boolean,
        override val code: Int,
        override val meta: Map<String, String>?,
        override val value: T?,
        override val msg: String? = null,
        override val err: Exception? = null,
        override val tag: String? = null
) : Response<T> {

    override fun withMeta(meta: List<Pair<String, String>>): Response<T> {
        return this.meta?.let {
            copy(meta = it.plus(meta))
        } ?: copy(meta = meta.toMap())
    }
}
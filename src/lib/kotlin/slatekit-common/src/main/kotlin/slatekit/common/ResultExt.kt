package slatekit.common

import slatekit.common.requests.Response
import slatekit.results.*


/**
 * Converts result to Response.
 */
fun <T, E> slatekit.results.Result<T, E>.toResponse(): Response<T> {
    return when (this) {
        is slatekit.results.Success -> Response(this.success, this.code, null, this.value, this.msg, null)
        is slatekit.results.Failure -> {
            val ex:Exception = when (this.error) {
                is Exception -> this.error as Exception
                else -> Exception(this.error.toString())
            }
            Response(this.success, this.code, null, null, this.msg, ex)
        }
    }
}

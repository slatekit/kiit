package slatekit.common

import slatekit.common.requests.Response
import slatekit.common.requests.SimpleResponse
import slatekit.results.*


/**
 * Converts result to Response.
 */
fun <T, E> slatekit.results.Result<T, E>.toResponse(): Response<T> {
    return when (this) {
        is slatekit.results.Success -> SimpleResponse(this.success, this.code, null, this.value, this.msg, null)
        is slatekit.results.Failure -> {
            val ex:Exception = when (this.error) {
                is Exception -> this.error as Exception
                else -> Exception(this.error.toString())
            }
            SimpleResponse(this.success, this.code, null, null, this.msg, ex)
        }
    }
}

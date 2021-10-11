package slatekit.requests

import slatekit.results.Status

/**
 * Converts result to Response.
 */
fun <T, E> slatekit.results.Result<T, E>.toResponse(): Response<T> {
    return when (this) {
        is slatekit.results.Success -> CommonResponse(this.success, this.status.name, Status.toType(this.status), this.status.code, null, this.value, this.desc, null)
        is slatekit.results.Failure -> {
            val ex: Exception = when (this.error) {
                is Exception -> this.error as Exception
                else -> Exception(this.error.toString())
            }
            CommonResponse(this.success, this.status.name, Status.toType(this.status), this.status.code, null, null, this.desc, ex)
        }
    }
}

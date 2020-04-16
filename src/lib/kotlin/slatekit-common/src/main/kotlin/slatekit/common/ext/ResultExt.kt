package slatekit.common.ext

import slatekit.common.requests.CommonResponse
import slatekit.common.requests.Response
import slatekit.results.*


/**
 * Converts result to Response.
 */
fun <T, E> slatekit.results.Result<T, E>.toResponse(): Response<T> {
    return when (this) {
        is slatekit.results.Success -> CommonResponse(this.success, this.code, null, this.value, this.msg, null)
        is slatekit.results.Failure -> {
            val ex: Exception = when (this.error) {
                is Exception -> this.error as Exception
                else -> Exception(this.error.toString())
            }
            CommonResponse(this.success, this.code, null, null, this.msg, ex)
        }
    }
}


/**
 * De-structured into key/value pairs for structured logging
 */
fun slatekit.results.Result<*, *>.structured(): List<Pair<String, Any?>> {
    return listOf(
            slatekit.results.Result<*, *>::success.name to this.success,
            slatekit.results.Result<*, *>::code.name to this.code,
            slatekit.results.Result<*, *>::msg.name to this.msg
    )
}


/**
 * Builds a list of key/value pairs from the Failure data and possible @see[slatekit.results.Err]
 * used for structured logging
 */
fun slatekit.results.Failure<*>.structured(): List<Pair<String, Any?>> {
    val err = this.error
    return when (err) {
        null -> listOf()
        is Err -> err.strings().mapIndexed { ndx, e -> "Error $ndx" to e }
        is ExceptionErr -> err.err.strings().mapIndexed { ndx, e -> "Error $ndx" to e }
        is StatusException -> listOf(StatusException::msg.name to err.msg)
        else -> listOf(StatusException::msg.name to err.toString())
    }
}

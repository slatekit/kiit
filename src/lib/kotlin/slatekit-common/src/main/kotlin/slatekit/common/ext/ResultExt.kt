package slatekit.common.ext

import slatekit.results.*


/**
 * De-structured into key/value pairs for structured logging
 */
fun slatekit.results.Result<*, *>.structured(): List<Pair<String, Any?>> {
    return listOf(
            slatekit.results.Result<*, *>::success.name to this.success,
            slatekit.results.Result<*, *>::code.name to this.code,
            slatekit.results.Result<*, *>::desc.name to this.desc
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

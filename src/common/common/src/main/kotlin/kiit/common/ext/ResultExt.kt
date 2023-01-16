package kiit.common.ext

import kiit.results.*


/**
 * De-structured into key/value pairs for structured logging
 */
fun kiit.results.Result<*, *>.structured(): List<Pair<String, Any?>> {
    return listOf(
            kiit.results.Result<*, *>::success.name to this.success,
            kiit.results.Result<*, *>::code.name to this.code,
            kiit.results.Result<*, *>::desc.name to this.desc
    )
}


/**
 * Builds a list of key/value pairs from the Failure data and possible @see[kiit.results.Err]
 * used for structured logging
 */
fun kiit.results.Failure<*>.structured(): List<Pair<String, Any?>> {
    val err = this.error
    return when (err) {
        null -> listOf()
        is Err -> err.strings().mapIndexed { ndx, e -> "Error $ndx" to e }
        is ExceptionErr -> err.err.strings().mapIndexed { ndx, e -> "Error $ndx" to e }
        is StatusException -> listOf(StatusException::msg.name to err.msg)
        else -> listOf(StatusException::msg.name to err.toString())
    }
}

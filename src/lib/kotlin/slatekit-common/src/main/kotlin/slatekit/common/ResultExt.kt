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


/**
 * Applies supplied function `f` if this is a [Success]
 *
 * @param f: the function to apply
 *
 * # Example
 * ```
 * val r1 = Success("Superman").flatMap { Success("Clark Kent") }  // Success("Clark Kent")
 * val r2 = Failure("Unknown" ).flatMap { Success("???")        }  // Failure("Unknown")
 * ```
 */
inline fun <T1, T2, E> Result<T1, E>.then(f: (T1) -> Result<T2, E>): Result<T2, E> =
        when (this) {
            is Success -> f(this.value)
            is Failure -> this
        }


fun <T,E> slatekit.results.Result<Result<T, E>,E>.inner(): Result<T,E> = this.fold( { it }, { Failure(it) } )

val EXIT    = StatusGroup.Errored(4001, "Exiting")
val HELP    = StatusGroup.Errored(4002, "Help")
val ABOUT   = StatusGroup.Errored(4003, "About")
val VERSION = StatusGroup.Errored(4004, "Version")
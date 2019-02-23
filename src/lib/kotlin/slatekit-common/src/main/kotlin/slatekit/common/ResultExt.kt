package slatekit.common

import slatekit.common.requests.Response
import slatekit.results.Failure
import slatekit.results.StatusGroup
import slatekit.results.Try


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

fun <T> slatekit.results.Try<Try<T>>.flatten(): Try<T> = this.fold( { it }, { Failure(it) } )

val EXIT    = StatusGroup.Errored(4001, "Exiting")
val HELP    = StatusGroup.Errored(4002, "Help")
val ABOUT   = StatusGroup.Errored(4003, "About")
val VERSION = StatusGroup.Errored(4004, "Version")
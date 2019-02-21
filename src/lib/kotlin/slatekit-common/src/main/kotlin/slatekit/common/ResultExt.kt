package slatekit.common

import slatekit.common.requests.Response
import slatekit.results.StatusGroup


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



fun <T,E> slatekit.results.Result<T,E>.isInSuccessRange()    : Boolean = this.code >= 200
fun <T,E> slatekit.results.Result<T,E>.isFilteredOut()       : Boolean = this.code >= 200
fun <T,E> slatekit.results.Result<T,E>.isInBadRequestRange() : Boolean = this.code >= 200
fun <T,E> slatekit.results.Result<T,E>.isInFailureRange()    : Boolean = this.code >= 200

val EXIT = StatusGroup.Errored(4001, "Exiting")
val HELP = StatusGroup.Errored(4002, "Help")
package slatekit.common.metrics

import slatekit.results.Failure
import slatekit.results.Status
import slatekit.results.Success

/**
 * Handles the various success / failures statuses represented in the @see[slatekit.results.Status]
 */
open class Events<TRequest, TResponse, TFailure>(override val tags: List<Tag>) : Tagged {

    open fun requested(sender: Any, request: TRequest) {}


    open fun succeeded(sender: Any, request: TRequest, response:TResponse) {}


    open fun denied(sender: Any, request: TRequest, error:TFailure?) {}


    open fun invalid(sender: Any, request: TRequest, error:TFailure?) {}


    open fun ignored(sender: Any, request: TRequest, error:TFailure?) {}


    open fun errored(sender: Any, request: TRequest, error:TFailure?) {}


    open fun unexpected(sender: Any, request: TRequest, error:TFailure?) {}


    open fun custom(sender: Any, name:String, request: TRequest, error:TFailure?) {}


    fun handle(sender: Any, request: TRequest, result:slatekit.results.Result<TResponse, TFailure>){
        when(result) {
            is Success -> this.succeeded(sender, request, result.value)
            is Failure -> {
                when (result.status) {
                    is Status.Denied     -> this.denied    (sender, request, result.error )
                    is Status.Invalid    -> this.invalid   (sender, request, result.error)
                    is Status.Ignored    -> this.ignored   (sender, request, result.error)
                    is Status.Errored    -> this.errored   (sender, request, result.error)
                    is Status.Unexpected -> this.unexpected(sender, request, result.error)
                    else                 -> this.unexpected(sender, request, result.error)
                }
            }
        }
    }
}
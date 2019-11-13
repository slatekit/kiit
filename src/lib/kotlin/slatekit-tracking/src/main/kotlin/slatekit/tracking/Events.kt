package slatekit.tracking

import slatekit.results.Failure
import slatekit.results.Result
import slatekit.results.Status
import slatekit.results.Success

/**
 * Handles the various success / failures statuses represented in the @see[slatekit.results.Status]
 */
open class Events<TRequest, TResponse, TFailure>(
    override val tags: List<Tag> = listOf(),
    val successConverter:((Any, TRequest, TResponse) -> Event)? = null,
    val failureConverter:((Any, TRequest, Failure<TFailure> ) -> Event)? = null,
    val eventHandler:((Event) -> Unit)? = null) : Tagged {

    open fun requested(sender: Any, request: TRequest) {}


    open fun succeeded(sender: Any, request: TRequest, response:TResponse) {
        handleSuccess(sender, request, response)
    }


    open fun denied(sender: Any, request: TRequest, error:Failure<TFailure>) {
        handleFailure(sender, request, error)
    }


    open fun invalid(sender: Any, request: TRequest, error:Failure<TFailure>) {
        handleFailure(sender, request, error)
    }


    open fun ignored(sender: Any, request: TRequest, error:Failure<TFailure>) {
        handleFailure(sender, request, error)
    }


    open fun errored(sender: Any, request: TRequest, error:Failure<TFailure>) {
        handleFailure(sender, request, error)
    }


    open fun unexpected(sender: Any, request: TRequest, error:Failure<TFailure>) {
        handleFailure(sender, request, error)
    }


    open fun custom(sender: Any, name:String, request: TRequest, error:Failure<TFailure>) {}


    protected fun handleSuccess(sender:Any, request: TRequest, response:TResponse){
        successConverter?.let { converter ->
            eventHandler?.let { handler ->
                val event = converter(sender, request, response)
                handler(event)
            }
        }
    }

    protected fun handleFailure(sender:Any, request: TRequest, error:Failure<TFailure>){
        error?.let { err ->
            failureConverter?.let { converter ->
                eventHandler?.let { handler ->
                    val event = converter(sender, request, error)
                    handler(event)
                }
            }
        }
    }


    open fun handle(sender: Any, request: TRequest, result:slatekit.results.Result<TResponse, TFailure>){
        when(result) {
            is Success -> this.succeeded(sender, request, result.value)
            is Failure -> {
                when (result.status) {
                    is Status.Denied     -> this.denied    (sender, request, result )
                    is Status.Invalid    -> this.invalid   (sender, request, result)
                    is Status.Ignored    -> this.ignored   (sender, request, result)
                    is Status.Errored    -> this.errored   (sender, request, result)
                    is Status.Unexpected -> this.unexpected(sender, request, result)
                    else                 -> this.unexpected(sender, request, result)
                }
            }
        }
    }
}

package slatekit.common.diagnostics

/**
 * Handles various events from the Manager/Worker such as status changes
 */
open class Events<TRequest, TResponse, TError> {

    open fun onRequest(sender: Any, request: TRequest) {
    }


    open fun onSuccess(sender: Any, request: TRequest, response:TResponse) {
    }


    open fun onFiltered(sender: Any, request: TRequest, response:TResponse) {
    }


    open fun onInvalid(sender: Any, request: TRequest, error:TError?) {
    }


    open fun onErrored(sender: Any, request: TRequest, error:TError?) {
    }


    open fun onEvent(sender: Any, request: TRequest, response:TResponse) {
    }
}

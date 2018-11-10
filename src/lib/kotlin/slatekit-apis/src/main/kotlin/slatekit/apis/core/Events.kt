package slatekit.apis.core

import slatekit.common.Request
import slatekit.common.Response

/**
 * Handles various events from the Manager/Worker such as status changes
 */
open class Events(val callback: ((Request, Response<*>) -> Unit)? = null) {

    open fun onReqest(sender: Any, request: Request) {
    }


    open fun onSuccess(sender: Any, request: Request, response:Response<*>) {
    }


    open fun onFiltered(sender: Any, request: Request, response:Response<*>) {
    }


    open fun onInvalid(sender: Any, request: Request, response:Response<*>) {
    }


    open fun onErrored(sender: Any, request: Request, response:Response<*>) {
    }


    open fun onEvent(sender: Any, request: Request, response:Response<*>) {
    }
}

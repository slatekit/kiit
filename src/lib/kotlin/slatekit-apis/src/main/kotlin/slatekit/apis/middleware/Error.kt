package slatekit.apis.middleware

import slatekit.common.*

/**
 * A "Filter" based middle-ware that either allows/disallows an API call to proceed
 *
 * NOTE: This is applied before conversion of request parameters( e.g. JSON )
 * to action parameters.
 */
interface Error : Middleware {

    /**
     * Filters the calls and returns a true/false indicating whether or not to proceed
     * @param ctx : The application context
     * @param req : The source to determine if it can be filtered
     * @param target: The target of the request
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param ex : The exception associated with error
     * @param args : Additional arguments supplied by the source
     */
    @Ignore
    fun onError(ctx: Context, req: Request, target: Any, source: Any, ex: Exception?, args: Map<String, Any>?): ResultEx<Any> {
        return Success("")
    }
}
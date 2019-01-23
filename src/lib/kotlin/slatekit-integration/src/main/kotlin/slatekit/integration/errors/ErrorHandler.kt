package slatekit.integration.errors

import slatekit.apis.core.Requests
import slatekit.apis.middleware.Error
import slatekit.common.*
import slatekit.common.requests.Request
import slatekit.integration.common.AppEntContext

class ErrorHandler(val ctx: AppEntContext, val queue: ErrorItemQueue, val enableEncryption: Boolean) : Error {

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
    override fun onError(ctx: Context, req: Request, target: Any, source: Any, ex: Exception?, args: Map<String, Any>?): ResultEx<Any> {

        return try {
            // Put the request into the
            val enc = if (enableEncryption) ctx.enc else null
            val jsonRequest = Requests.toJson(req, enc)

            // Create an error item out of the request
            val error = ErrorItem(
                    source = source.toString(),
                    action = req.fullName,
                    error = ex?.toString() ?: "Error processing ${req.fullName}",
                    request = jsonRequest,
                    tag = req.tag
            )
            queue.send(error)
        } catch (ex: Exception) {
            Failure(ex, msg = "Error storing request for processing")
        }
    }
}
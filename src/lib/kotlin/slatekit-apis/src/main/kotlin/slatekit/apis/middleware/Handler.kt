/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.apis.middleware

import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.info.About


/**
 * A "Control" based middle-ware that takes over the execution
 * of an API call right at the point of execution.
 *
 * @param about  : Info about the the filter
 * @param route  : The route pattern to apply this middleware to
 */
open class Handler(override val about: About, val route: Match) : Middleware() {


    /**
     * handles the api action, can return various results indicating to the
     * container whether or not to proceed with the call.
     * e.g.
     * 1. Success => tells container to proceed making the api call
     * 2. Failure => tells container to not make the call, and flag as an error
     * @param ctx   : The application context
     * @param req   : The request
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args  : Additional arguments supplied by the source
     */
    open fun handle(ctx: Context, req: Request, source: Any, args: Map<String, Any>): Result<Any> {
        return success
    }
}

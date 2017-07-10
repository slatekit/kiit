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

package slatekit.core.middleware

import slatekit.common.Request
import slatekit.common.info.About
import slatekit.core.common.AppContext


/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of an API call, without any modification to the life-cycle/flow.
 *
 * NOTE: The hooks are applied right before and after the call to the action,
 * allowing parameters to the action to be made available.
 *
 * @param about  : Info about the the filter
 * @param route  : The route pattern to apply this middleware to
 */
class Hook(
        override val about: About,
        val route: Match
) : Middleware() {

    /**
     * hook for before the api call is made
     * @param ctx   : The application context
     * @param req   : The request
     * @param target: The target of the request
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args  : Additional arguments supplied by the source
     */
    fun before(ctx: AppContext, req: Request, target:Any, source: Any, args: Map<String, Any>): Unit {
    }


    /**
     * hook for after the api call is made
     * @param ctx   : The application context
     * @param req   : The request
     * @param target: The target of the request
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args  : Additional arguments supplied by the source
     */
    fun after(ctx: AppContext, req: Request, target:Any, source: Any, args: Map<String, Any>): Unit {
    }
}

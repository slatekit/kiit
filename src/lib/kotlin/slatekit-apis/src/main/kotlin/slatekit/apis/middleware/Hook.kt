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

import slatekit.apis.ApiRegAction
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.info.About


/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of an API call, without any modification to the life-cycle/flow.
 *
 * NOTE: The hooks are applied right before and after the call to the action
 *
 */
interface Hook : Middleware {

    /**
     * hook for before the api call is made
     * @param ctx   : The application context
     * @param req   : The request
     * @param target: The target of the request
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args  : Additional arguments supplied by the source
     */
    fun onBefore(ctx: Context, req: Request, target: ApiRegAction, source: Any, args: Map<String, Any>?): Unit {
    }


    /**
     * hook for after the api call is made
     * @param ctx   : The application context
     * @param req   : The request
     * @param target: The target of the request
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args  : Additional arguments supplied by the source
     */
    fun onAfter(ctx: Context, req: Request, target:ApiRegAction, source: Any, args: Map<String, Any>?): Unit {
    }
}

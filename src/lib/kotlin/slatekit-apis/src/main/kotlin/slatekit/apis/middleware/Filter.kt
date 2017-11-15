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
 * A "Filter" based middle-ware that either allows/disallows an API call
 * without any modification to the life-cycle/flow.
 *
 * NOTE: This is applied before conversion of request parameters( e.g. JSON )
 * to action parameters.
 *
 * @param about  : Info about the the filter
 * @param route  : The route pattern to apply this middleware to
 */
open class Filter(override val about: About, val route: Match) : Middleware() {


    /**
     * Filters the calls and returns a true/false indicating whether or not to proceed
     * @param ctx   : The application context
     * @param req   : The source to determine if it can be filtered
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args  : Additional arguments supplied by the source
     */
    open fun filter(ctx: Context, req: Request, source: Any, args: Map<String, Any>): Result<Any> {
        return success
    }
}

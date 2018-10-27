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

import slatekit.common.*
import slatekit.common.results.ResultFuncs.success

/**
 * A "Filter" based middle-ware that either allows/disallows an API call to proceed
 *
 * NOTE: This is applied before conversion of request parameters( e.g. JSON )
 * to action parameters.
 */
interface Filter : Middleware {

    /**
     * Filters the calls and returns a true/false indicating whether or not to proceed
     * @param ctx : The application context
     * @param req : The source to determine if it can be filtered
     * @param source: The originating source for this hook ( e.g. ApiContainer )
     * @param args : Additional arguments supplied by the source
     */
    @Ignore
    fun onFilter(ctx: Context, req: Request, source: Any, args: Map<String, Any>?): ResultMsg<Any> {
        return success("")
    }
}

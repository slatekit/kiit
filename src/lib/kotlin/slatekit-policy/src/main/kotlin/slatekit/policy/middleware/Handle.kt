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

package slatekit.policy.middleware

import slatekit.common.*
import slatekit.results.Outcome

/**
 * A middle-ware that handles the request
 *
 */
interface Handle<TReq, TRes> : Middleware {

    /**
     * Handles a call
     * @param req : The request
     */
    @Ignore
    suspend fun handle(req: TReq, op: suspend (TReq) -> Outcome<TRes>): Outcome<TRes>
}

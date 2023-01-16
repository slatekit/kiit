/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.policy.hooks

import kiit.common.*
import kiit.results.Outcome

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

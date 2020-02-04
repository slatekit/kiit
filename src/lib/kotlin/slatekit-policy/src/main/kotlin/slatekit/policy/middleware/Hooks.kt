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

/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of an API call, without any modification to the life-cycle/flow.
 *
 * NOTE: The hooks are applied right before and after the call to the action
 *
 */
interface Hooks<TReq, TRes> : Middleware, Before<TReq>, After<TReq, TRes>

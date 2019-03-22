/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.apis.middleware

import slatekit.apis.core.Action
import slatekit.common.Context
import slatekit.common.Ignore
import slatekit.common.requests.Request

/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of an API call, without any modification to the life-cycle/flow.
 *
 * NOTE: The hooks are applied right before and after the call to the action
 *
 */
interface After : Middleware {

    /**
     * hook for after the api call is made
     * @param ctx : The application context
     * @param req : The request
     * @param target: The target of the request
     * @param source: The originating source for this hook ( e.g. ApiHost )
     * @param args : Additional arguments supplied by the source
     */
    @Ignore
    fun onAfter(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?) {
    }
}

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
package slatekit.apis.core

import slatekit.apis.ApiRequest
import slatekit.apis.ApiServer
import slatekit.context.Context
import slatekit.common.requests.Request

data class Ctx(val host: ApiServer, val context: Context, val req: Request, val target: Target) {

    companion object {
        fun of(host: ApiServer, ctx: Context, request: ApiRequest):Ctx {
            return Ctx(host, ctx, request.request, request.target!!)
        }
    }
}

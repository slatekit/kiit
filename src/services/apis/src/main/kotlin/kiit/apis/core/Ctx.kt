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
package kiit.apis.core

import kiit.apis.ApiRequest
import kiit.apis.ApiServer
import kiit.context.Context
import kiit.requests.Request

data class Ctx(val host: ApiServer, val context: Context, val req: Request, val target: Target) {

    companion object {
        fun of(host: ApiServer, ctx: Context, request: ApiRequest):Ctx {
            return Ctx(host, ctx, request.request, request.target!!)
        }
    }
}

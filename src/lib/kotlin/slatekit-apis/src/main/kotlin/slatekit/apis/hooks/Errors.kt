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
package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.policy.middleware.Failed
import slatekit.results.Outcome

class Errors {


    companion object {

        suspend fun applyError(raw:ApiRequest, apiReq: ApiRequest, req:Outcome<ApiRequest>, res: Outcome<ApiResult>) {
            val inst = apiReq.target?.instance
            if(inst is slatekit.policy.middleware.Failed<*, *>) {
                val middleware = inst as slatekit.policy.middleware.Failed<ApiRequest, ApiResult>
                Failed.handle(middleware, raw, req, res)
            }
        }
    }
}

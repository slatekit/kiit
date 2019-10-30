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
import slatekit.common.Ignore
import slatekit.common.log.Logger
import slatekit.functions.Output
import slatekit.results.Err
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

class Errors {


    companion object {

        suspend fun applyError(raw:ApiRequest, apiReq: ApiRequest, req:Outcome<ApiRequest>, res: Outcome<ApiResult>) {
            val inst = apiReq.target?.instance
            if(inst is slatekit.functions.middleware.Error<*, *>) {
                val beforeHook = inst as slatekit.functions.middleware.Error<ApiRequest, ApiResult>
                beforeHook.onDone(raw, req, res)
            }
        }
    }
}

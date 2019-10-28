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
import slatekit.common.log.Logger
import slatekit.functions.Output
import slatekit.results.Err
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

open class Errors(val logger: Logger) : Output<ApiRequest, ApiResult> {

    override suspend fun process(request: ApiRequest, result: Outcome<ApiResult>): Outcome<ApiResult> {
        result.onFailure { handle(request, result, it) }
        return result
    }

    private suspend fun handle(request: ApiRequest, result: Outcome<ApiResult>, err: Err) {
        when {
            request.target != null && request.target.instance is slatekit.functions.middleware.Error<*, *> -> {
                logger.info("Handling error at api level")
                val error = Outcomes.errored<ApiRequest>(err)
                val errors = request.target.instance as slatekit.functions.middleware.Error<ApiRequest, Any?>
                errors.onError(request, result)
            }
            else -> {
                logger.debug("Handling error at global middleware")
            }
        }
    }
}

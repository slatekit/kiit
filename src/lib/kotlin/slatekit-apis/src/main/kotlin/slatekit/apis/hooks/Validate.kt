package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.Middleware
import slatekit.apis.core.Calls
import slatekit.common.Ignore
import slatekit.results.Outcome

/**
 * Checks parameters in the request are valid with respective the target ( method )
 */
class Validate : Middleware {

    @Ignore
    override suspend fun process(req: ApiRequest, next:suspend(ApiRequest) -> Outcome<Any>): Outcome<Any> {
        val checkResult = Calls.validateCall(req, { r -> req.host.get(r) }, true)
        return if (!checkResult.success) {
            // Don't return the result from internal ( as it contains too much info )
            checkResult.map { req }
        } else {
            next(req)
        }
    }
}

package slatekit.apis.services

import slatekit.apis.ApiRequest
import slatekit.apis.Middleware
import slatekit.common.Ignore
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

/**
 * Checks that the route/path is valid ( mapped to a method target )
 */
class Targets : Middleware {

    @Ignore
    override suspend fun process(req: ApiRequest, next:suspend(ApiRequest) -> Outcome<Any>): Outcome<Any> {

        val request = req.request
        val result = req.host.get(request.area, request.name, request.action)
        return when (result) {
            is Success -> next(req.copy(target = result.value))
            is Failure -> Outcomes.errored(result.error)
        }
    }
}

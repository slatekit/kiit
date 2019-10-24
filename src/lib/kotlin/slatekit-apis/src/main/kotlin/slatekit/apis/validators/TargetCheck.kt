package slatekit.apis.validators

import slatekit.apis.ApiRequest
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

/**
 * Checks that the route/path is valid ( mapped to a method target )
 */
class TargetCheck {
    fun check(request: ApiRequest): Outcome<ApiRequest> {
        val req = request.request
        val result = request.host.getApi(req.area, req.name, req.action)
        return when(result) {
            is Success -> Outcomes.success(request)
            is Failure -> Outcomes.errored(result.error)
        }
    }
}
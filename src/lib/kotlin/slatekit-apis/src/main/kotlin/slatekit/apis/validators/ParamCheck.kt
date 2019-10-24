package slatekit.apis.validators

import slatekit.apis.ApiRequest
import slatekit.apis.helpers.ApiValidator
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Checks parameters in the request are valid with respective the target ( method )
 */
class ParamCheck {

    fun check(request: ApiRequest): Outcome<ApiRequest> {
        val req = request.request
        val checkResult = ApiValidator.validateCall(req, { r -> request.host.get(r) }, true)
        return if (!checkResult.success) {
            // Don't return the result from internal ( as it contains too much info )
            Outcomes.invalid(checkResult.msg)
        } else {
            checkResult.transform({ s -> Outcomes.success(request)}, { e -> Outcomes.errored(e)})
        }
    }
}
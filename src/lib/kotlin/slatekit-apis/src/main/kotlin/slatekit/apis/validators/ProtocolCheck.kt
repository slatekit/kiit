package slatekit.apis.validators

import slatekit.apis.ApiConstants
import slatekit.apis.ApiRequest
import slatekit.apis.helpers.ApiHelper
import slatekit.apis.helpers.ApiUtils
import slatekit.apis.setup.Protocol
import slatekit.results.Codes
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes



/**
 * Checks the protocol of the request matches the allowed protocols on the action/api
 */
class ProtocolCheck {

    fun check(request: ApiRequest): Outcome<ApiRequest> {
        // Ensure verb is correct get/post
        val req = request.request
        val target = request.target!!
        val actualVerb = ApiUtils.getReferencedValue(target.action.verb.name, target.api.verb.name)
        val actualProtocol = ApiUtils.getReferencedValue(target.action.protocol.name, target.api.protocol.name)
        val isCliOk = ApiUtils.isCliAllowed(actualProtocol)
        val isWeb = request.host.protocol == Protocol.Web

        // 1. Ensure verb is correct
        return if (isWeb && req.verb == Protocol.Queue.name) {
            Outcomes.success(request)
        } else if (isWeb && !ApiHelper.isValidMatch(actualVerb, req.verb)) {
            Outcomes.errored("expected verb $actualVerb, but got ${req.verb}")
        }
        // 2. Ensure protocol is correct get/post
        else if (!isCliOk && !ApiHelper.isValidMatch(actualProtocol, request.host.protocol.name)) {
            Outcomes.errored("${req.fullName} not found", Codes.NOT_FOUND)
        }
        // 3. Good to go
        else
            Outcomes.success(request)
    }
}
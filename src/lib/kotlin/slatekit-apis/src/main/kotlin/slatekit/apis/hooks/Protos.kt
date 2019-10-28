package slatekit.apis.hooks

import slatekit.apis.ApiConstants
import slatekit.apis.ApiRequest
import slatekit.apis.Protocol
import slatekit.apis.core.Protocols
import slatekit.common.Ignore
import slatekit.common.Strings
import slatekit.functions.Input
import slatekit.results.Codes
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap

/**
 * Checks the protocol of the request matches the allowed protocols on the action/api
 */
class Protos : Input<ApiRequest> {

    @Ignore
    override suspend fun process(request: Outcome<ApiRequest>): Outcome<ApiRequest> {
        return request.flatMap {
            // Ensure verb is correct get/post
            val req = it.request
            val target = it.target!!
            val actualVerb = getReferencedValue(target.action.verb.name, target.api.verb.name)
            val actualProtocol = getReferencedValue(target.action.protocol.name, target.api.protocol.name)
            val isCliOk = Protocols.isCLI(actualProtocol)
            val isWeb = it.host.settings.protocol == Protocol.Web

            // 1. Ensure verb is correct
            return if (isWeb && req.verb == Protocol.Queue.name) {
                request
            } else if (isWeb && !Strings.isMatchOrWildCard(actualVerb, req.verb)) {
                Outcomes.errored("expected verb $actualVerb, but got ${req.verb}")
            }
            // 2. Ensure protocol is correct get/post
            else if (!isCliOk && !Strings.isMatchOrWildCard(actualProtocol, it.host.settings.protocol.name)) {
                Outcomes.errored("${req.fullName} not found", Codes.NOT_FOUND)
            }
            // 3. Good to go
            else
                request
        }
    }

    fun getReferencedValue(primaryValue: String, parentValue: String): String {

        // Role!
        return if (!primaryValue.isNullOrEmpty()) {
            if (primaryValue == ApiConstants.parent) {
                parentValue
            } else
                primaryValue
        }
        // Parent!
        else if (!parentValue.isNullOrEmpty()) {
            parentValue
        } else
            ""
    }
}

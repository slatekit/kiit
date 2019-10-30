package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.Protocol
import slatekit.apis.Verb
import slatekit.apis.core.Protocols
import slatekit.common.Ignore
import slatekit.common.requests.Request
import slatekit.functions.Input
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
        val actionVerb = target.action.verb.orElse(target.api.verb)
        val actionProtocols = target.action.protocols.orElse(target.api.protocols)
        val isCli = actionProtocols.hasCLI()
        val isWeb = actionProtocols.hasWeb()

        val verbResult = validateVerb(isWeb, isCli, actionVerb, req, request)
        val finalResult = verbResult.flatMap { validateProto(actionProtocols, req, request) }
        finalResult
        }
    }

    private fun validateVerb(isWeb:Boolean, isCLI:Boolean, actionVerb: Verb, req: Request, request:Outcome<ApiRequest>):Outcome<ApiRequest> {
        return when {
            // Case 1: Queued request, being processed
            req.verb == Protocol.Queue.name -> request

            // Case 2: Web, ensure verb match
            isWeb && actionVerb.isMatch(req.verb) -> request

            // Case 3: CLI, doesn't matter
            isCLI -> request

            // Case 4: invalid
            else -> Outcomes.errored("expected verb $actionVerb, but got ${req.verb}")
        }
    }

    private fun validateProto(actionProtocols: Protocols, req: Request, request:Outcome<ApiRequest>):Outcome<ApiRequest> {
        val requestProtocol = Protocol.parse(req.source)
        return when {
            actionProtocols.isMatchOrAll(requestProtocol) -> request
            else -> {
                val oneOf = actionProtocols.all.joinToString { it.name }
                Outcomes.errored("expected protocol $oneOf, but got ${req.source.id}")
            }
        }
    }
}

package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.Verb
import slatekit.common.Ignore
import slatekit.common.Source
import slatekit.common.requests.Request
import slatekit.policy.Input
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap

/**
 * Checks the source of the request matches the allowed sources on the action/api
 */
class Protos : Input<ApiRequest> {

    @Ignore
    override suspend fun process(i: Outcome<ApiRequest>): Outcome<ApiRequest> {
        return i.flatMap {
        // Ensure verb is correct get/post
        val req = it.request
        val target = it.target!!
        val actionVerb = target.action.verb.orElse(target.api.verb)
        val actionProtocols = target.action.sources.orElse(target.api.sources)
        val isCli = actionProtocols.hasCLI()
        val isWeb = actionProtocols.hasWeb()

        val verbResult = validateVerb(isWeb, isCli, actionVerb, req, i)
        val finalResult = verbResult.flatMap { validateProto(actionProtocols, req, i) }
        finalResult
        }
    }

    private fun validateVerb(isWeb:Boolean, isCLI:Boolean, actionVerb: Verb, req: Request, request:Outcome<ApiRequest>):Outcome<ApiRequest> {
        return when {
            // Case 1: Queued request, being processed
            req.verb == Source.Queue.id -> request

            // Case 2: Web, ensure verb match
            isWeb && actionVerb.isMatch(req.verb) -> request

            // Case 3: CLI, doesn't matter
            isCLI -> request

            // Case 4: invalid
            else -> Outcomes.errored("expected verb $actionVerb, but got ${req.verb}")
        }
    }

    private fun validateProto(actionProtocols: slatekit.apis.core.Sources, req: Request, request:Outcome<ApiRequest>):Outcome<ApiRequest> {
        val requestProtocol = req.source
        return when {
            actionProtocols.isMatchOrAll(requestProtocol) -> request
            else -> {
                val oneOf = actionProtocols.all.joinToString { it.id }
                Outcomes.errored("expected source $oneOf, but got ${req.source.id}")
            }
        }
    }
}

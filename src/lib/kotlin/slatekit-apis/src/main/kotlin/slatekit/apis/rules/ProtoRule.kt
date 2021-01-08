package slatekit.apis.rules

import slatekit.apis.ApiRequest
import slatekit.apis.Verb
import slatekit.common.Source
import slatekit.common.requests.Request
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap

object ProtoRule : Rule {

    override fun validate(req: ApiRequest): Outcome<Boolean> {
        // Ensure verb is correct get/post
        val request = req.request
        val target = req.target!!
        val actionVerb = target.action.verb.orElse(target.api.verb)
        val actionProtocols = target.action.sources.orElse(target.api.sources)
        val isCli = actionProtocols.hasCLI()
        val isApi = actionProtocols.hasAPI()

        val verbResult = validateVerb(isApi, isCli, actionVerb, request, req)
        val finalResult = verbResult.flatMap { validateProto(actionProtocols, request, req) }
        return finalResult.map { true }
    }


    private fun validateVerb(isWeb: Boolean, isCLI: Boolean, actionVerb: Verb, req: Request, request: ApiRequest): Outcome<ApiRequest> {
        return when {
            // Case 1: Queued request, being processed
            req.verb == Source.Queue.id -> Outcomes.success(request)

            // Case 2: Web, ensure verb match
            isWeb && actionVerb.isMatch(req.verb) -> Outcomes.success(request)

            // Case 3: CLI, doesn't matter
            isCLI -> Outcomes.success(request)

            // Case 4: invalid
            else -> Outcomes.errored("expected verb $actionVerb, but got ${req.verb}")
        }
    }

    private fun validateProto(actionProtocols: slatekit.apis.core.Sources, req: Request, request: ApiRequest): Outcome<ApiRequest> {
        val requestProtocol = req.source
        return when {
            actionProtocols.isMatchOrAll(requestProtocol) -> Outcomes.success(request)
            else -> {
                val oneOf = actionProtocols.all.joinToString { it.id }
                Outcomes.errored("expected source $oneOf, but got ${req.source.id}")
            }
        }
    }
}

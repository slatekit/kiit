package kiit.apis.rules

import kiit.apis.ApiRequest
import kiit.apis.Verb
import kiit.apis.Verbs
import kiit.common.Source
import kiit.requests.Request
import kiit.results.Outcome
import kiit.results.builders.Outcomes
import kiit.results.flatMap

object ProtoRule : Rule {

    override fun validate(req: ApiRequest): Outcome<Boolean> {
        // Ensure verb is correct get/post
        val request = req.request
        val target = req.target!!
        val actionVerb = target.action.verb.orElse(target.api.verb)
        val actionProtocols = target.action.sources.orElse(target.api.sources)
        val hasCli = actionProtocols.hasCLI()
        val hasApi = actionProtocols.hasAPI()

        val verbResult = validateVerb(hasApi, hasCli, actionVerb, request, req)
        val finalResult = verbResult.flatMap { validateProto(actionProtocols, request, req) }
        return finalResult.map { true }
    }


    private fun validateVerb(hasWeb: Boolean, hasCLI: Boolean, actionVerb: Verb, req: Request, request: ApiRequest): Outcome<ApiRequest> {
        val source = request.host.settings.source
        return when {
            // Case 1: Queued request, being processed
            req.verb == Source.Queue.id -> Outcomes.success(request)

            // Case 2: Web, auto handle
            source == Source.API -> {
                when(actionVerb.isMatch(req.verb)) {
                    true -> Outcomes.success(request)
                    false -> Outcomes.denied("Incorrect verb, expected=${actionVerb.name}, actual=${req.verb}")
                }
            }

            // Case 3: CLI, doesn't matter
            source == Source.CLI -> Outcomes.success(request)

            // Case 4: ALL, doesn't matter
            source == Source.All -> Outcomes.success(request)

            // Case 5: invalid
            else -> Outcomes.errored("expected verb $actionVerb, but got ${req.verb}")
        }
    }

    private fun validateProto(actionProtocols: kiit.apis.core.Sources, req: Request, request: ApiRequest): Outcome<ApiRequest> {
        val requestProtocol = req.source
        return when {
            actionProtocols.isMatchOrAll(requestProtocol) -> Outcomes.success(request)
            else -> {
                val oneOf = actionProtocols.all.joinToString { it.id }
                Outcomes.errored("expected source $oneOf, but got ${req.source.id}")
            }
        }
    }


    fun validate(name:String, verb:String):Boolean {
        return when {
            name.startsWith("create") -> verb == Verbs.POST
            name.startsWith("insert") -> verb == Verbs.POST
            name.startsWith("update") -> verb == Verbs.PUT
            name.startsWith("modify") -> verb == Verbs.PUT
            name.startsWith("remove") -> verb == Verbs.DELETE
            name.startsWith("delete") -> verb == Verbs.DELETE
            name.startsWith("patch")  -> verb == Verbs.PATCH
            name.startsWith("get")    -> verb == Verbs.GET
            name.startsWith("fetch")  -> verb == Verbs.GET
            else -> false
        }
    }
}

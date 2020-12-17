package slatekit.apis.rules

import slatekit.apis.ApiRequest
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

object RouteRule : Rule {
    override fun validate(req: ApiRequest): Outcome<Boolean> {
        // e.g. "users.invite" = [ "users", "invite" ]
        // Check 1: at least 2 parts
        val totalParts = req.request.parts.size
        if (totalParts < 2) {
            return Outcomes.invalid(req.request.action + ": invalid call")
        }
        val request = req.request
        val result = req.host.get(request.area, request.name, request.action)
        return result.map { true }
    }
}

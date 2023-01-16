package kiit.apis.rules

import kiit.apis.ApiRequest
import kiit.results.Outcome
import kiit.results.builders.Outcomes

object RouteRule {

    fun isValid(req: ApiRequest): Boolean {
        // e.g. "users.invite" = [ "users", "invite" ]
        // Check 1: at least 2 parts
        val totalParts = req.request.parts.size
        return totalParts >= 2
    }
}

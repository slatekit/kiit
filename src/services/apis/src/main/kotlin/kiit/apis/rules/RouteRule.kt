package kiit.apis.rules

import kiit.apis.ApiRequest
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

object RouteRule {

    fun isValid(req: ApiRequest): Boolean {
        // e.g. "users.invite" = [ "users", "invite" ]
        // Check 1: at least 2 parts
        val totalParts = req.request.parts.size
        return totalParts >= 2
    }
}

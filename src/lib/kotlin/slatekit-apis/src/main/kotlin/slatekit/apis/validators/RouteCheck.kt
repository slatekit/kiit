package slatekit.apis.validators

import slatekit.apis.ApiRequest
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


/**
 * Checks that the route/path is valid in that it has an area/api
 */
class RouteCheck {
    fun check(request: ApiRequest): Outcome<ApiRequest> {
        val req = request.request
        // e.g. "users.invite" = [ "users", "invite" ]
        // Check 1: at least 2 parts
        val totalParts = req.parts.size
        return if (totalParts < 2) {
            Outcomes.invalid(req.action + ": invalid call")
        } else {
            Outcomes.success(request)
        }
    }
}
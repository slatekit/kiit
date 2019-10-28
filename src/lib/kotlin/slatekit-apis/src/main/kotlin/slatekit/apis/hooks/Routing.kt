package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.common.Ignore
import slatekit.functions.Input
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap


/**
 * Checks that the route/path is valid in that it has an area/api
 */
class Routing : Input<ApiRequest> {

    @Ignore
    override suspend fun process(request: Outcome<ApiRequest>):Outcome<ApiRequest> {
        return request.flatMap {
            val req = it.request
            // e.g. "users.invite" = [ "users", "invite" ]
            // Check 1: at least 2 parts
            val totalParts = req.parts.size
            return if (totalParts < 2) {
                Outcomes.invalid(req.action + ": invalid call")
            } else {
                request
            }
        }
    }
}



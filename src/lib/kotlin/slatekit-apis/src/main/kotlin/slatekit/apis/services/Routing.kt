package slatekit.apis.services

import slatekit.apis.ApiRequest
import slatekit.apis.Middleware
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


class Routing : Middleware {
    override suspend fun process(req: ApiRequest, next: suspend (ApiRequest) -> Outcome<Any>):Outcome<Any> {
        // e.g. "users.invite" = [ "users", "invite" ]
        // Check 1: at least 2 parts
        val totalParts = req.request.parts.size
        return if (totalParts < 2) {
            Outcomes.invalid(req.request.action + ": invalid call")
        } else {
            next(req)
        }
    }
}

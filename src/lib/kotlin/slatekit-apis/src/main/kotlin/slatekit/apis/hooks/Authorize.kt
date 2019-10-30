package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.core.Auth
import slatekit.common.Ignore
import slatekit.functions.Input
import slatekit.functions.middleware.Middleware
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap

/**
 * Checks Authorization of the request
 */
class Authorize(val auth: Auth?) : Input<ApiRequest>, Middleware {

    @Ignore
    override suspend fun process(request: Outcome<ApiRequest>): Outcome<ApiRequest> {
        return request.flatMap {
            val target = it.target!!
            val noAuth = auth == null // || target.api.auth.isNullOrEmpty()
            val actionRoles = target.action.roles.orElse(target.api.roles)
            val isAuthed = actionRoles.isAuthed

            // CASE 1: No auth for action
            val result = if (noAuth && !isAuthed) {
                request
            }
            // CASE 2: No auth and action requires roles!
            else if (noAuth) {
                Outcomes.denied("Unable to authorize, authorization provider not set")
            }
            // CASE 3: Proceed to authorize
            else {
                val authResult = auth?.check(it.request, target.api.auth, actionRoles)
                        ?: Outcomes.denied("Unable to authorize, authorization provider not set")
                authResult.flatMap { request }
            }
            result
        }
    }
}

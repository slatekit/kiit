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
class Authorize(val auth:Auth?) : Input<ApiRequest>, Middleware {

    @Ignore
    override suspend fun process(request: Outcome<ApiRequest>):Outcome<ApiRequest> {
        return request.flatMap {
            val target = it.target!!
            val noAuth = auth == null // || target.api.auth.isNullOrEmpty()
            val isActionNotAuthed = target.action.roles.isAuthed
            val isApiNotAuthed = target.api.roles.isAuthed

            // CASE 1: No auth for action
            val result = if (noAuth && isActionNotAuthed) {
                request
            }
            // CASE 2: No auth for parent
            else if (noAuth && isApiNotAuthed) {
                request
            }
            // CASE 3: No auth and action requires roles!
            else if (noAuth) {
                Outcomes.denied("Unable to authorize, authorization provider not set")
            } else {
                // auth-mode, action roles, api roles
                val authResult = auth?.check(it.request, target.api.auth, target.action.roles, target.api.roles)
                        ?: Outcomes.denied("Unable to authorize, authorization provider not set")
                authResult.transform({ request }, { e -> Outcomes.errored(e) })
            }
            result
        }
    }
}



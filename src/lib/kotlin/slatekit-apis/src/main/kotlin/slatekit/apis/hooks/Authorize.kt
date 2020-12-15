package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.Middleware
import slatekit.apis.core.Auth
import slatekit.common.Ignore
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

/**
 * Checks Authorization of the request
 */
class Authorize(val auth: Auth?) : Middleware {

    @Ignore
    override suspend fun process(req: ApiRequest, next:suspend(ApiRequest) -> Outcome<Any>): Outcome<Any> {

        val target = req.target!!
        val noAuth = auth == null // || target.api.auth.isNullOrEmpty()
        val actionRoles = target.action.roles.orElse(target.api.roles)
        val isAuthed = actionRoles.isAuthed

        // CASE 1: No auth for action
        return if (noAuth && !isAuthed) {
            next(req)
        }
        // CASE 2: No auth and action requires roles!
        else if (noAuth) {
            Outcomes.denied("Unable to authorize, authorization provider not set")
        }
        // CASE 3: Proceed to authorize
        else {
            val authResult = auth?.check(req.request, target.action.auth, actionRoles)
                    ?: Outcomes.denied("Unable to authorize, authorization provider not set")
            when(authResult){
                is Success -> next(req)
                is Failure -> authResult
            }
        }
    }
}

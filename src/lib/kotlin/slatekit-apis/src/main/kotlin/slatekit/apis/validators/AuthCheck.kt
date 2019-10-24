package slatekit.apis.validators

import slatekit.apis.ApiRequest
import slatekit.apis.helpers.ApiHelper
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Notices
import slatekit.results.builders.Outcomes

/**
 * Checks Authorization of the request
 */
class AuthCheck {

    fun check(request: ApiRequest): Outcome<ApiRequest> {
        val auth = request.host.auth
        val target = request.target!!
        val noAuth = auth == null // || target.api.auth.isNullOrEmpty()
        val isActionNotAuthed = ApiHelper.isActionNotAuthed(target.action.roles.all.first())
        val isApiNotAuthed = ApiHelper.isApiNotAuthed(target.action.roles.all.first(), target.api.roles.all.first())

        // CASE 1: No auth for action
        val result = if (noAuth && isActionNotAuthed) {
            Outcomes.success(request)
        }
        // CASE 2: No auth for parent
        else if (noAuth && isApiNotAuthed) {
            Outcomes.success(request)
        }
        // CASE 3: No auth and action requires roles!
        else if (noAuth) {
            Outcomes.denied("Unable to authorize, authorization provider not set")
        } else {
            // auth-mode, action roles, api roles
            val authResult = auth?.check(request.request, target.api.auth, target.action.roles, target.api.roles)
                    ?: Outcomes.denied("Unable to authorize, authorization provider not set")
            authResult.transform( { Outcomes.success(request) }, { e -> Outcomes.errored(e) } )
        }

        return when(result) {
            is Success -> Outcomes.success(request)
            is Failure -> Outcomes.errored(result.error)
        }
    }
}



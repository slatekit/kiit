package slatekit.apis.rules

import slatekit.apis.ApiRequest
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes


object AuthRule : Rule {
    override fun validate(req:ApiRequest): Outcome<Boolean> {
        val target = req.target!!
        val noAuth = req.auth == null // || target.api.auth.isNullOrEmpty()
        val actionRoles = target.action.roles.orElse(target.api.roles)
        val isAuthed = actionRoles.isAuthed

        // CASE 1: No auth for action
        return if (noAuth && !isAuthed) {
            Outcomes.success(true)
        }
        // CASE 2: No auth and action requires roles!
        else if (noAuth) {
            Outcomes.denied("Auth not configured")
        }
        // CASE 3: Proceed to authorize
        else {
            req.auth?.check(req.request, target.action.auth, actionRoles)
                ?: Outcomes.denied("Auth not configured")
        }
    }
}

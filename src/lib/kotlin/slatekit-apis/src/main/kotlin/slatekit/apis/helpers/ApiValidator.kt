/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.apis.helpers

import slatekit.apis.core.Target
import slatekit.apis.core.Action
import slatekit.common.*
import slatekit.common.requests.Request
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.builders.Notices

object ApiValidator {

    /**
     * Checks the "route" ( area.api.action ) is valid.
     */
    fun check(req: Request, fetcher: (Request) -> Notice<Target>): Notice<Target> {
        // e.g. "users.invite" = [ "users", "invite" ]
        // Check 1: at least 2 parts
        val totalParts = req.parts.size
        return if (totalParts < 2) {
           Notices.invalid(req.action + ": invalid call")
        } else {
            // Check 2: Not found ?
            val check = fetcher(req)
            check
        }
    }

    /**
     * whether or not the api call represented by the area.api.action exists. e.g. "app.users.invite"
     * and the parameters are valid.
     *
     * @param req : the command input
     * @return
     */
    fun validateCall(
            req: Request,
            fetcher: (Request) -> Notice<Target>,
            allowSingleDefaultParam: Boolean = false
    ): Notice<Target> {
        val fullName = req.fullName
        val args = req.data
        val apiRefCheck = check(req, fetcher)

        return when (apiRefCheck) {
            is Failure -> Notices.invalid("bad request : $fullName: inputs not supplied")
            is Success -> {
                val apiRef = apiRefCheck.value
                val action = apiRef.action

                // 1 param with default argument.
                if (allowSingleDefaultParam && action.isSingleDefaultedArg() && args.size() == 0) {
                    Success(apiRef)
                }
                // Param: Raw ApiCmd itself!
                else if (action.isSingleArg() && action.paramsUser.isEmpty()) {
                    Success(apiRef)
                }
                // Params - check args needed
                else if (!allowSingleDefaultParam && action.hasArgs && args.size() == 0)
                    Notices.invalid<Target>("bad request : $fullName: inputs not supplied")

                // Params - ensure matching args
                else if (action.hasArgs) {
                    val argCheck = validateArgs(action, args)
                    if (argCheck.success) {
                        Success(apiRef)
                    } else
                        Notices.invalid<Target>("bad request : $fullName: inputs not supplied")
                } else
                    Success(apiRef)
            }
        }
    }

    private fun validateArgs(action: Action, args: Inputs): Notice<Boolean> {
        var error = ": inputs missing or invalid "
        var totalErrors = 0

        // Check each parameter to api call
        for (index in 0 until action.paramsUser.size) {
            val input = action.paramsUser[index]
            // parameter not supplied ?
            val paramName = input.name!!
            if (!args.containsKey(paramName)) {
                val separator = if (totalErrors == 0) "( " else ","
                error += separator + paramName
                totalErrors += 1
            }
        }
        // Any errors ?
        return if (totalErrors > 0) {
            error = "$error )"
            Notices.invalid("bad request: action " + action.name + error)
        } else {
            Success(true)
        }
    }
}

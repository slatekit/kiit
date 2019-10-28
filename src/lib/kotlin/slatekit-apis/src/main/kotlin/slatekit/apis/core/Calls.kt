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

package slatekit.apis.core

import slatekit.apis.ApiRequest
import slatekit.apis.hooks.Targets
import slatekit.common.*
import slatekit.common.requests.Request
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Notices
import slatekit.results.builders.Outcomes

object Calls {

    /**
     * whether or not the api call represented by the area.api.action exists. e.g. "app.users.invite"
     * and the parameters are valid.
     *
     * @param req : the command input
     * @return
     */
    suspend fun validateCall(
            request: ApiRequest,
            fetcher: (Request) -> Notice<Target>,
            allowSingleDefaultParam: Boolean = false
    ): Outcome<Target> {
        val req = request.request
        val fullName = req.fullName
        val args = req.data
        val apiRefCheck = Targets().process(Outcomes.of(request))

        return when (apiRefCheck) {
            is Failure -> Outcomes.invalid("bad request : $fullName: inputs not supplied")
            is Success -> {
                val apiRef = apiRefCheck.value
                val target = apiRef.target!!
                val action = target.action

                // 1 param with default argument.
                if (allowSingleDefaultParam && action.isSingleDefaultedArg() && args.size() == 0) {
                    Outcomes.success(target)
                }
                // Param: Raw ApiCmd itself!
                else if (action.isSingleArg() && action.paramsUser.isEmpty()) {
                    Outcomes.success(target)
                }
                // Data - check args needed
                else if (!allowSingleDefaultParam && action.hasArgs && args.size() == 0)
                    Outcomes.invalid("bad request : $fullName: inputs not supplied")

                // Data - ensure matching args
                else if (action.hasArgs) {
                    val argCheck = validateArgs(action, args)
                    if (argCheck.success) {
                        Outcomes.success(target)
                    } else
                        Outcomes.invalid("bad request : $fullName: inputs not supplied")
                } else
                    Outcomes.success(target)
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

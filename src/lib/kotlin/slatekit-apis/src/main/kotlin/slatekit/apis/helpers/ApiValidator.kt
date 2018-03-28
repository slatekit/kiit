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

import slatekit.apis.ApiRef
import slatekit.apis.ApiRegAction
import slatekit.common.Inputs
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success


object ApiValidator {

    /**
     * Checks the "route" ( area.api.action ) is valid.
     */
    fun check(cmd: Request, fetcher: (Request) -> Result<ApiRef>) : Result<ApiRef> {
        // e.g. "users.invite" = [ "users", "invite" ]
        // Check 1: at least 2 parts
        val totalParts = cmd.parts.size
        return if (totalParts < 2) {
           badRequest(cmd.action + ": invalid call")
        }
        else {
            // Check 2: Not found ?
            val check = fetcher(cmd)
            check
        }
    }


    /**
     * whether or not the api call represented by the area.api.action exists. e.g. "app.users.invite"
     * and the parameters are valid.
     *
     * @param req       : the command input
     * @return
     */
    fun validateCall(req: Request,
                     fetcher: (Request) -> Result<ApiRef>,
                     allowSingleDefaultParam: Boolean = false): Result<ApiRef> {
        val fullName = req.fullName
        val args = req.data
        val apiRefCheck = check(req, fetcher)

        return if (!apiRefCheck.success) {
            badRequest(msg = "bad request : $fullName: inputs not supplied")
        }
        else {
            val apiRef = apiRefCheck.value!!
            val action = apiRef.action

            // 1 param with default argument.
            if (allowSingleDefaultParam && action.isSingleDefaultedArg() && args!!.size() == 0) {
                success(apiRef)
            }
            // Param: Raw ApiCmd itself!
            else if (action.isSingleArg() && action.paramsUser.isEmpty()) {
                success(apiRef)
            }
            // Params - check args needed
            else if (!allowSingleDefaultParam && action.hasArgs && args!!.size() == 0)
                badRequest(msg = "bad request : $fullName: inputs not supplied")

            // Params - ensure matching args
            else if (action.hasArgs) {
                val argCheck = validateArgs(action, args!!)
                if (argCheck.success) {
                    success(apiRef)
                }
                else
                    badRequest(msg = "bad request : $fullName: inputs not supplied")
            }
            else
                success(apiRef)
        }
    }


    private fun validateArgs(action: ApiRegAction, args: Inputs): Result<Boolean> {
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
            badRequest(msg = "bad request: action " + action.name + error)
        }
        else {
            ok()
        }
    }
}

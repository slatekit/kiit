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

package slatekit.apis.support

import slatekit.apis.ApiBase
import slatekit.apis.core.Action
import slatekit.common.Inputs
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success


object ApiValidator {

    /**
     * Checks the "route" ( area.api.action ) is valid.
     */
    fun check(cmd: Request, fetcher: (Request) -> Result<Pair<Action, ApiBase>>)
            : Triple<Boolean, Result<Any>, Result<Pair<Action, ApiBase>>?> {
        // e.g. "users.invite" = [ "users", "invite" ]
        // Check 1: at least 2 parts
        val totalParts = cmd.parts.size
        return if (totalParts < 2) {
            Triple(false, badRequest<ApiBase>(cmd.action + ": invalid call"), null)
        }
        else {
            // Check 2: Not found ?
            val check = fetcher(cmd)
            val result = if (check.success) success(true) else failure(msg = check.msg)

            Triple(check.success, result, check)
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
                     fetcher: (Request) -> Result<Pair<Action, ApiBase>>,
                     allowSingleDefaultParam: Boolean = false): Result<ApiBase> {
        val fullName = req.fullName
        val args = req.args
        val checkResult = check(req, fetcher)

        return if (!checkResult.first) {
            badRequest<ApiBase>(msg = "bad request : $fullName: inputs not supplied")
        }
        else {
            val result = checkResult.third?.value!!
            val callReflect = result.first
            val api = result.second

            // 1 param with default argument.
            if (allowSingleDefaultParam && callReflect.isSingleDefaultedArg() && args!!.size() == 0) {
                success(api)
            }
            // Param: Raw ApiCmd itself!
            else if (callReflect.isSingleArg() && callReflect.paramList[0].type.toString() == "Request") {
                success(api)
            }
            // Params - check args needed
            else if (!allowSingleDefaultParam && callReflect.hasArgs && args!!.size() == 0)
                badRequest<ApiBase>(msg = "bad request : " + fullName + ": inputs not supplied")

            // Params - ensure matching args
            else if (callReflect.hasArgs) {
                val argCheck = validateArgs(callReflect, args!!)
                if (argCheck.success) {
                    success(api)
                }
                else
                    badRequest<ApiBase>(msg = "bad request : " + fullName + ": inputs not supplied")
            }
            else
                success(api)
        }
    }


    fun validateArgs(action: Action, args: Inputs): Result<Boolean> {
        var error = ": inputs missing or invalid "
        var totalErrors = 0

        // Check each parameter to api call
        for (index in 1..action.paramList.size - 1) {
            val input = action.paramList[index]
            // parameter not supplied ?
            val paramName = input.name!!
            if (!args.containsKey(paramName)) {
                val separator = if (totalErrors == 0) "( " else ","
                error += separator + paramName
                totalErrors = totalErrors + 1
            }
        }
        // Any errors ?
        return if (totalErrors > 0) {
            error = error + " )"
            badRequest(msg = "bad request: action " + action.name + error)
        }
        else {
            // Ok!
            ok()
        }
    }
}

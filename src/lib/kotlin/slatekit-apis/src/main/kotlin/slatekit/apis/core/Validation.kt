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

import slatekit.apis.ApiBase
import slatekit.apis.ApiConstants
import slatekit.apis.ApiContainer
import slatekit.apis.ApiProtocolWeb
import slatekit.apis.support.ApiHelper
import slatekit.apis.support.ApiValidator
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.success


class Validation(val ctn: ApiContainer) {


    fun validateApi(cmd: Request): Result<Pair<Action, ApiBase>> {
        return ctn.getMappedAction(cmd.area, cmd.name, cmd.action)
    }


    fun validateProtocol(callReflect: Action, api: ApiBase, cmd: Request): Result<Any> {
        // Ensure verb is correct get/post
        val actualVerb = getReferencedValue(callReflect.action.verb, callReflect.api.verb)
        val actualProtocol = getReferencedValue(callReflect.action.protocol, callReflect.api.protocol)
        val supportedProtocol = actualProtocol
        val isCliOk = ctn.isCliAllowed(cmd, supportedProtocol)
        val isWeb = ctn.protocol == ApiProtocolWeb

        // 1. Ensure verb is correct
        return if (isWeb && !ApiHelper.isValidMatch(actualVerb, cmd.verb)) {
            badRequest(msg = "expected verb ${actualVerb}, but got ${cmd.verb}")
        }

        // 2. Ensure protocol is correct get/post
        else if (!isCliOk && !ApiHelper.isValidMatch(supportedProtocol, ctn.protocol.name)) {
            notFound(msg = "${cmd.fullName} not found")
        }
        // 3. Good to go
        else
            success(cmd)
    }


    fun validateMiddleware(cmd: Request): Result<Any> {
        return success(cmd)
    }


    fun validateAuthorization(callReflect: Action, cmd: Request): Result<Any> {
        return ApiHelper.isAuthorizedForCall(cmd, callReflect, ctn.auth)
    }


    fun validateParameters(cmd: Request): Result<ApiBase> {
        val checkResult = ApiValidator.validateCall(cmd, { req -> ctn.get(cmd) }, true)
        return if (!checkResult.success) {
            // Don't return the result from internal ( as it contains too much info )
            badRequest(checkResult.msg, tag = cmd.action)
        }
        else
            checkResult
    }


    fun getReferencedValue(primaryValue: String, parentValue: String): String {

        // Role!
        return if (!primaryValue.isNullOrEmpty()) {
            if (primaryValue == ApiConstants.RoleParent) {
                parentValue
            }
            else
                primaryValue
        }
        // Parent!
        else if (!parentValue.isNullOrEmpty()) {
            parentValue
        }
        else
            ""
    }
}

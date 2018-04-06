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

import slatekit.apis.*
import slatekit.apis.helpers.ApiHelper
import slatekit.apis.helpers.ApiValidator
import slatekit.apis.middleware.Filter
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.successOrError


class Validation(val ctn: ApiContainer) {


    fun validateApi(req: Request): Result<ApiRef> {
        return ctn.getApi(req.area, req.name, req.action)
    }


    fun validateProtocol(req: Request, apiRef:ApiRef): Result<Any> {
        // Ensure verb is correct get/post
        val action = apiRef.action
        val api = apiRef.api
        val actualVerb = getReferencedValue(action.verb, api.verb)
        val actualProtocol = getReferencedValue(action.protocol, api.protocol)
        val supportedProtocol = actualProtocol
        val isCliOk = ctn.isCliAllowed(supportedProtocol)
        val isWeb = ctn.protocol == WebProtocol

        // 1. Ensure verb is correct
        return if (isWeb && !ApiHelper.isValidMatch(actualVerb, req.verb)) {
            badRequest(msg = "expected verb ${actualVerb}, but got ${req.verb}")
        }

        // 2. Ensure protocol is correct get/post
        else if (!isCliOk && !ApiHelper.isValidMatch(supportedProtocol, ctn.protocol.name)) {
            notFound(msg = "${req.fullName} not found")
        }
        // 3. Good to go
        else
            success(req)
    }


    fun validateMiddleware(req: Request, apiRef:ApiRef): Result<Any> {
        val filters = ctn.middleware?.filter { it is Filter }?.map { it as Filter } ?: listOf()
        val failed = filters.fold( success(""), { acc, filter ->
            if(acc.success) {
                acc
            } else {
                filter.onFilter(ctn.ctx, req, ctn, null).map( { _ -> "" })
            }
        })
        return successOrError(failed.success, failed.value, failed.msg)
    }


    fun validateAuthorization(req: Request, apiRef:ApiRef): Result<Any> {
        return ApiHelper.isAuthorizedForCall(req, apiRef, ctn.auth)
    }


    fun validateParameters(req: Request): Result<ApiRef> {
        val checkResult = ApiValidator.validateCall(req, { req -> ctn.get(req) }, true)
        return if (!checkResult.success) {
            // Don't return the result from internal ( as it contains too much info )
            badRequest(checkResult.msg, tag = req.action)
        }
        else
            checkResult
    }


    fun getReferencedValue(primaryValue: String, parentValue: String): String {

        // Role!
        return if (!primaryValue.isNullOrEmpty()) {
            if (primaryValue == ApiConstants.Parent) {
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

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
import slatekit.apis.helpers.ApiUtils
import slatekit.apis.helpers.ApiValidator
import slatekit.apis.security.WebProtocol
import slatekit.common.requests.Request
import slatekit.results.*
import slatekit.results.builders.Notices

class Validation(val ctn: ApiHost) {

    fun validateApi(req: Request): Notice<ApiRef> {
        return ctn.getApi(req.area, req.name, req.action)
    }

    fun validateProtocol(req: Request, apiRef: ApiRef): Notice<Request> {
        // Ensure verb is correct get/post
        val action = apiRef.action
        val api = apiRef.api
        val actualVerb = getReferencedValue(action.verb, api.verb)
        val actualProtocol = getReferencedValue(action.protocol, api.protocol)
        val supportedProtocol = actualProtocol
        val isCliOk = ApiUtils.isCliAllowed(supportedProtocol)
        val isWeb = ctn.protocol == WebProtocol

        // 1. Ensure verb is correct
        return if (isWeb && req.verb == ApiConstants.SourceQueue) {
            Success(req)
        } else if (isWeb && !ApiHelper.isValidMatch(actualVerb, req.verb)) {
            Failure("expected verb $actualVerb, but got ${req.verb}")
        }

        // 2. Ensure protocol is correct get/post
        else if (!isCliOk && !ApiHelper.isValidMatch(supportedProtocol, ctn.protocol.name)) {
            Notices.errored<Request>("${req.fullName} not found", Codes.NOT_FOUND)
        }
        // 3. Good to go
        else
            Success(req)
    }

    fun validateMiddleware(req: Request, filters: List<Filter>): Notice<Any> {
        val failed = filters.fold(Success<Any>("") as Notice<Any>) { acc, filter ->
            if (!acc.success) {
                acc
            } else {
                filter.onFilter(ctn.ctx, req, ctn, null).toNotice()
            }
        }
        return failed
    }

    fun validateAuthorization(req: Request, apiRef: ApiRef): Notice<Any> {
        return ApiHelper.isAuthorizedForCall(req, apiRef, ctn.auth)
    }

    fun validateParameters(request: Request): Notice<ApiRef> {
        val checkResult = ApiValidator.validateCall(request, { req -> ctn.get(req) }, true)
        return if (!checkResult.success) {
            // Don't return the result from internal ( as it contains too much info )
            Notices.invalid(checkResult.msg)
        } else
            checkResult
    }

    fun getReferencedValue(primaryValue: String, parentValue: String): String {

        // Role!
        return if (!primaryValue.isNullOrEmpty()) {
            if (primaryValue == ApiConstants.parent) {
                parentValue
            } else
                primaryValue
        }
        // Parent!
        else if (!parentValue.isNullOrEmpty()) {
            parentValue
        } else
            ""
    }
}

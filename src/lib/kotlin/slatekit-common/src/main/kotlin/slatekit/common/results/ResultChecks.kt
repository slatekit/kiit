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

package slatekit.common.results

import slatekit.common.results.ResultCode.BAD_REQUEST
import slatekit.common.results.ResultCode.CONFLICT
import slatekit.common.results.ResultCode.DEPRECATED
import slatekit.common.results.ResultCode.EXIT
import slatekit.common.results.ResultCode.FAILURE
import slatekit.common.results.ResultCode.HELP
import slatekit.common.results.ResultCode.NOT_AVAILABLE
import slatekit.common.results.ResultCode.NOT_FOUND
import slatekit.common.results.ResultCode.NOT_IMPLEMENTED
import slatekit.common.results.ResultCode.UNAUTHORIZED
import slatekit.common.results.ResultCode.UNEXPECTED_ERROR

interface ResultChecks {

    fun statusCode(): Int

    val isFailure: Boolean get() = statusCode() == FAILURE
    val isUnAuthorized: Boolean get() = statusCode() == UNAUTHORIZED
    val isNotFound: Boolean get() = statusCode() == NOT_FOUND
    val isBadRequest: Boolean get() = statusCode() == BAD_REQUEST
    val isConflict: Boolean get() = statusCode() == CONFLICT
    val isDeprecated: Boolean get() = statusCode() == DEPRECATED
    val isUnexpectedError: Boolean get() = statusCode() == UNEXPECTED_ERROR
    val isNotImplemented: Boolean get() = statusCode() == NOT_IMPLEMENTED
    val isNotAvailable: Boolean get() = statusCode() == NOT_AVAILABLE
    val isHelp: Boolean get() = statusCode() == HELP
    val isExit: Boolean get() = statusCode() == EXIT
}

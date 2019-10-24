/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.apis.core

import slatekit.apis.ApiRequest
import slatekit.common.Context
import slatekit.common.requests.Request
import slatekit.common.log.Logger
import slatekit.results.Outcome
import slatekit.results.Try
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

open class Errors(val logger: Logger, val errs: slatekit.functions.middleware.Error<ApiRequest, Any?>?) {

    suspend open fun handleError(req:ApiRequest, ex: Exception): Outcome<Any> {
        val target = req.target
        return when {
            target == null -> {
                // OPTION 1: GLOBAL Level default handler
                // logger.debug("Handling error at global container")
                handleErrorInternally(req.request, ex)
            }
            target.instance is slatekit.functions.middleware.Error<*,*> -> {
                logger.info("Handling error at api level")
                val error = Outcomes.errored<ApiRequest>(ex)
                val errors = target.instance as slatekit.functions.middleware.Error<ApiRequest,Any?>
                errors.onError(req, error)
                error
            }
            else -> {
                logger.debug("Handling error at global middleware")
                val error = Outcomes.errored<ApiRequest>(ex)
                errs?.onError(req, error)
                error
            }
        }
    }


    /**
     * global handler for an unexpected error ( for derived classes to override )
     *
     * @param ctx : the application context
     * @param req : the request
     * @param ex : the exception
     * @return
     */
    fun handleErrorInternally(req: Request, ex: Exception): Outcome<Any> {
        val msg = "error executing : " + req.path + ", check inputs"
        return Outcomes.unexpected(Exception(msg, ex))
    }
}

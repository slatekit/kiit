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

import slatekit.apis.ApiRef
import slatekit.common.Context
import slatekit.common.Request
import slatekit.common.ResultEx
import slatekit.common.log.Logger
import slatekit.common.results.ResultFuncs
import slatekit.common.toResultEx

class Errors(val logger: Logger) {

    open fun handleError(
        ctx: Context,
        errs: slatekit.apis.middleware.Error?,
        api: slatekit.apis.core.Api?,
        apiRef: ApiRef?,
        req: Request,
        ex: Exception
    ): ResultEx<Any> {
        // OPTION 1: Api level
        return if (apiRef != null && apiRef.instance is slatekit.apis.middleware.Error) {
            logger.debug("Handling error at api level")
            apiRef.instance.onError(ctx, req, apiRef, this, ex, null).toResultEx()
        }
        // OPTION 2: GLOBAL Level custom handler
        else if (errs != null) {
            logger.debug("Handling error at global middleware")
            errs.onError(ctx, req, req.path, this, ex, null).toResultEx()
        }
        // OPTION 3: GLOBAL Level default handler
        else {
            logger.debug("Handling error at global container")
            handleErrorInternally(req, ex)
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
    fun handleErrorInternally(req: Request, ex: Exception): ResultEx<Any> {
        val msg = "error executing : " + req.path + ", check inputs"
        return ResultFuncs.unexpectedError(Exception(msg, ex))
    }
}

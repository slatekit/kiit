package slatekit.apis.support

import slatekit.apis.ApiServer
import slatekit.apis.Verb
import slatekit.common.requests.CommonRequest
import slatekit.common.requests.Request
import slatekit.common.requests.Response
import slatekit.common.ext.toResponse
import slatekit.results.Outcome
import slatekit.results.Try

interface ExecSupport {

    fun host(): ApiServer

    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun respond(req: Request): Response<Any> {
        return host().call(req, null).toResponse()
    }

    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun outcome(req: Request): Outcome<Any> {
        return host().call(req, null).toOutcome()
    }

    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun attempt(req: Request): Try<Any> {
        return host().call(req, null)
    }

    /**
     * Call with inputs instead of the request
     */
    suspend fun call(
        area: String,
        api: String,
        action: String,
        verb: Verb,
        opts: Map<String, Any>,
        args: Map<String, Any>
    ): Try<Any> {
        val req = CommonRequest.cli(area, api, action, verb.name, opts, args)
        return host().call(req, null)
    }
}

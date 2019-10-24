package slatekit.apis.support

import slatekit.apis.ApiHost
import slatekit.common.requests.Request
import slatekit.common.requests.Response
import slatekit.common.toResponse
import slatekit.results.Outcome
import slatekit.results.Try

interface ApiExecSupport {

    fun host(): ApiHost


    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun response(req: Request): Response<Any> {
        return host().callAsResult(req).toResponse()
    }


    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun outcome(req: Request): Outcome<Any> {
        return host().callAsResult(req).toOutcome()
    }


    /**
     * calls the api/action associated with the request
     * @param req
     * @return
     */
    suspend fun attempt(req: Request): Try<Any> {
        return host().callAsResult(req)
    }
}
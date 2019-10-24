package slatekit.apis.middleware

import slatekit.apis.ApiRequest
import slatekit.common.Ignore
import slatekit.functions.Input
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap

/**
 * Checks that the route/path is valid ( mapped to a method target )
 */
class Targets : Input<ApiRequest> {

    @Ignore
    override suspend fun process(request:Outcome<ApiRequest>):Outcome<ApiRequest> {
        return request.flatMap {
            val req = it.request
            val result = it.host.getApi(req.area, req.name, req.action)
            when (result) {
                is Success -> request
                is Failure -> Outcomes.errored(result.error)
            }
        }
    }
}
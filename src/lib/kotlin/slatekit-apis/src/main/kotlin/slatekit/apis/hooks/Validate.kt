package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.core.Calls
import slatekit.common.Ignore
import slatekit.functions.Input
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap

/**
 * Checks parameters in the request are valid with respective the target ( method )
 */
class Validate : Input<ApiRequest> {

    @Ignore
    override suspend fun process(request:Outcome<ApiRequest>):Outcome<ApiRequest> {
        return request.flatMap {
            val checkResult = Calls.validateCall(it, { r -> it.host.get(r) }, true)
            if (!checkResult.success) {
                // Don't return the result from internal ( as it contains too much info )
                Outcomes.invalid(checkResult.msg)
            } else {
                checkResult.transform({ s -> request }, { e -> Outcomes.errored(e) })
            }
        }
    }
}
package slatekit.apis.rules

import slatekit.apis.ApiRequest
import slatekit.apis.services.Calls
import slatekit.results.Outcome

object ParamsRule : Rule {
    override fun validate(req: ApiRequest): Outcome<Boolean> {
        val checkResult = Calls.validateCall(req, { r -> req.host.get(r) }, true)
        return checkResult.map { true }
    }
}

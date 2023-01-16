package kiit.apis.rules

import kiit.apis.ApiRequest
import kiit.apis.services.Calls
import kiit.results.Outcome

object ParamsRule : Rule {
    override fun validate(req: ApiRequest): Outcome<Boolean> {
        val checkResult = Calls.validateCall(req, { r -> req.host.get(r) }, true)
        return checkResult.map { true }
    }
}

package kiit.apis.rules

import kiit.apis.ApiRequest
import kiit.apis.services.Calls
import kiit.results.Outcome
import kiit.results.builders.Outcomes

object ParamsRule : Rule {
    override fun validate(req: ApiRequest): Outcome<Boolean> {
//        val checkResult = Calls.validateCall(req, { r -> req.host.get(r) }, true)
//        return checkResult.map { true }
        return Outcomes.success(true)
    }
}

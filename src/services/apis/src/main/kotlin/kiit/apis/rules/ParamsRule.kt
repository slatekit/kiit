package kiit.apis.rules

import kiit.apis.ApiRequest
import kiit.apis.routes.MethodExecutor
import kiit.apis.services.Calls
import kiit.results.Outcome
import kiit.results.builders.Outcomes

object ParamsRule : Rule {
    override fun validate(req: ApiRequest): Outcome<Boolean> {
        val checkResult = Calls.validateCall(req, true)
        return checkResult.map { true }
    }
}

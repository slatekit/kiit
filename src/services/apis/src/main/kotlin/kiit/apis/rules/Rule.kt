package kiit.apis.rules

import kiit.apis.ApiRequest
import kiit.results.Outcome

interface Rule {
    fun validate(req: ApiRequest): Outcome<Boolean>
}

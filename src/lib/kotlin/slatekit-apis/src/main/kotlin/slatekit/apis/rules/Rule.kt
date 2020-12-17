package slatekit.apis.rules

import slatekit.apis.ApiRequest
import slatekit.results.Outcome

interface Rule {
    fun validate(req: ApiRequest): Outcome<Boolean>
}

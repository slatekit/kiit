package slatekit.apis.validators

import kotlinx.coroutines.runBlocking
import slatekit.apis.ApiRequest
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Checks all the custom filters supplied
 */
class FilterCheck {

    fun check(req: ApiRequest): Outcome<ApiRequest> {
        val filters = req.host.filters
        val failed = runBlocking {
            filters.firstOrNull { !it.onFilter(req).success }
        }
        return when(failed) {
            null -> Outcomes.success(req)
            else -> {
                val err = runBlocking { failed.onFilter(req) }
                err.transform({ Outcomes.success(req)}, { e -> Outcomes.errored(e)})
            }
        }
    }
}
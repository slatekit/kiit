package slatekit.apis.middleware

import kotlinx.coroutines.runBlocking
import slatekit.apis.ApiRequest
import slatekit.common.Ignore
import slatekit.functions.Input
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.flatMap

/**
 * Checks all the custom filters supplied
 */
class Filters : Input<ApiRequest> {

    @Ignore
    override suspend fun process(req: Outcome<ApiRequest>):Outcome<ApiRequest> {
        return req.flatMap { apiReq ->
            val filters = apiReq.host.settings.inputters
            val failed = filters.firstOrNull { !it.process(req).success }
            when (failed) {
                null -> req
                else -> {
                    val err = runBlocking { failed.process(req) }
                    err.transform({ req }, { e -> Outcomes.errored(e) })
                }
            }
        }
    }
}
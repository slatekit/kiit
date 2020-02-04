package slatekit.apis

import slatekit.policy.Input
import slatekit.policy.Output
import slatekit.policy.Process
import slatekit.policy.middleware.Middleware

/**
 * Created by kreddy on 3/25/2016.
 */
data class ApiHooks(
    /**
     * Middleware to convert the input/incoming request
     * e.g. (Outcome<ApiRequest>) -> Outcome<ApiRequest>
     */
    val formatters: List<Input<ApiRequest>> = listOf(),

    /**
     * Middleware to check the input/incoming request
     * e.g. (Outcome<ApiRequest>) -> Outcome<ApiRequest>
     */
    val inputters: List<Input<ApiRequest>> = listOf(),

    /**
     * To execute the API requests
     * e.g (ApiRequest, (ApiRequest) -> Outcome<ApiResult>) -> Outcome<ApiResult>
     */
    val middleware: List<Handler> = listOf(),

    /**
     * Middleware to convert the output/outgoing result
     * * e.g. (ApiRequest, Outcome<ApiResult>) -> Outcome<ApiResult>
     */
    val outputter: List<Output<ApiRequest, ApiResult>> = listOf()
) {

        companion object {

            fun of(middleware: List<Middleware>): ApiHooks {
                return ApiHooks(
                        inputters = middleware.filter { it is Input<*> }.map { it as Input<ApiRequest> },
                        middleware = middleware.filter { it is Process<*, *> }.map { it as Handler },
                        outputter = middleware.filter { it is Output<*, *> }.map { it as Output<ApiRequest, ApiResult> }
                )
            }
        }
}

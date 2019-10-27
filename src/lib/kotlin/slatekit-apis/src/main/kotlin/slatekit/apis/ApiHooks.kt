package slatekit.apis

import slatekit.functions.Input
import slatekit.functions.Output


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
)
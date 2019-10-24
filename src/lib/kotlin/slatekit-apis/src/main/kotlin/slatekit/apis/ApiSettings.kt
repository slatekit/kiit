/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.apis

import slatekit.functions.Input
import slatekit.functions.Output


/**
 * Created by kreddy on 3/25/2016.
 */
data class ApiSettings(

        /**
         * Middleware to convert the input/incoming request
         * e.g. (Outcome<ApiRequest>) -> Outcome<ApiRequest>
         */
        val inputters: List<Input<ApiRequest>> = listOf(),


        /**
         * To validate, transform the API requests
         * e.g (ApiRequest, (ApiRequest) -> Outcome<ApiResult>) -> Outcome<ApiResult>
         */
        val middleware: List<Handler> = listOf(),


        /**
         * Middleware to convert the output/outgoing result
         * * e.g. (ApiRequest, Outcome<ApiResult>) -> Outcome<ApiResult>
         */
        val outputter: List<Output<ApiRequest, ApiResult>> = listOf()
)
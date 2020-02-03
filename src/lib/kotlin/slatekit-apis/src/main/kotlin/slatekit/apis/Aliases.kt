package slatekit.apis

import slatekit.policy.Process


/**
 * Middleware to handle a request both before and after it is executed
 */
typealias Hooks = slatekit.policy.middleware.Hooks<ApiRequest, ApiResult>

/**
 * Middleware to check / filter a request
 */
typealias Filter = slatekit.policy.middleware.Filter<ApiRequest>

/**
 * Middleware to handle a failed request
 */
typealias Error = slatekit.policy.middleware.Failed<ApiRequest, ApiResult>

/**
 * Middleware to handle a request
 */
typealias Handler = Process<ApiRequest, ApiResult>




package slatekit.apis

/**
 * Middleware to handle a request before it is executed
 */
typealias Before = slatekit.functions.middleware.Before<ApiRequest>

/**
 * Middleware to check / filter a request
 */
typealias Filter = slatekit.functions.middleware.Filter<ApiRequest>

/**
 * Middleware to handle a request after it is executed
 */
typealias After = slatekit.functions.middleware.After<ApiRequest, ApiResult>

/**
 * Middleware to handle a failed request
 */
typealias Error = slatekit.functions.middleware.Error<ApiRequest, ApiResult>

/**
 * Middleware to handle a request both before and after it is executed
 */
typealias Hooks = slatekit.functions.middleware.Hooks<ApiRequest, ApiResult>

/**
 * Middleware to handle a request
 */
typealias Handler = slatekit.functions.Process<ApiRequest, ApiResult>

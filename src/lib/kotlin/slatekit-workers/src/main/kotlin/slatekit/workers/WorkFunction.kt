package slatekit.workers

import slatekit.common.ResultEx

/**
 * Type Alias for the function inputs/outputs representing a work method.
 * Workers can be created with either a simple anonymous function or
 * derived from Worker.
 */
typealias WorkFunction<T> = (Job) -> ResultEx<T>

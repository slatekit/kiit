package slatekit.core.workers.core

import slatekit.common.ResultMsg

/**
 * Type Alias for the function inputs/outputs representing a work method.
 * Workers can be created with either a simple anonymous function or
 * derived from Worker.
 */
typealias WorkFunction<T> = (Array<Any>?) -> ResultMsg<T>
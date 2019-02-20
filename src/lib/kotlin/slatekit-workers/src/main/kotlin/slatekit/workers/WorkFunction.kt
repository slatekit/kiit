package slatekit.workers

import slatekit.results.Try

/**
 * Type Alias for the function inputs/outputs representing a work method.
 * Workers can be created with either a simple anonymous function or
 * derived from Worker.
 */
typealias WorkFunction<T> = (Job) -> Try<T>

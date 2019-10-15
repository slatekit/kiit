package slatekit.functions.common

import slatekit.common.Status

/**
 * Stores the state of the function execution
 */
interface FunctionState<out T> where T: FunctionResult {
    /**
     * Information about the function
     */
    val info: FunctionInfo

    /**
     * Function [slatekit.common.Status]
     */
    val status: Status

    /**
     * The last result of running the function
     */
    val lastResult: T?

    /**
     * The last message from the result
     */
    fun msg(): String = lastResult?.message ?: ""

    /**
     * Whether or not the function has been run
     */
    fun hasRun(): Boolean = lastResult != null
}
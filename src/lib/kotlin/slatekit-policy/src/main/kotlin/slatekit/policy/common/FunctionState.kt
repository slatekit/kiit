package slatekit.policy.common

import slatekit.common.Identity
import slatekit.common.Status
import slatekit.results.Outcome

/**
 * Stores the state of the function execution
 */
interface FunctionState<out T> where T : FunctionResult {
    /**
     * Information about the function
     */
    val id: Identity

    /**
     * Function [slatekit.common.Status]
     */
    val status: Status

    /**
     * The last result of running the function
     */
    val lastResult: Outcome<T>?

    /**
     * The last message from the result
     */
    fun msg(): String = lastResult?.desc ?: ""

    /**
     * Whether or not the function has been run
     */
    fun hasRun(): Boolean = lastResult != null
}

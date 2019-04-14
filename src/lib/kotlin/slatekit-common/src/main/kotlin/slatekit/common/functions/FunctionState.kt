package slatekit.common.functions

import slatekit.common.DateTime
import slatekit.common.Status


/**
 * Stores the state of the function execution
 */
interface FunctionState<out T> {
    /**
     * Information about the function
     */
    val info: FunctionInfo

    /**
     * Function [slatekit.common.Status]
     */
    val status: Status

    /**
     * The last message from the result
     */
    val msg: String

    /**
     * Last time the function was run
     */
    val lastRun: DateTime

    /**
     * Last mode of operation ( e.g. interactive, scheduled, triggered )
     */
    val lastMode: FunctionMode

    /**
     * Whether or not the function has been run
     */
    val hasRun: Boolean

    /**
     * Number of times the function has been run
     */
    val runCount: Long

    /**
     * Number of times the function errored out
     */
    val errorCount: Long

    /**
     * The last result of running the function
     */
    val lastResult: T?
}
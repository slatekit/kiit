package slatekit.common.functions

import slatekit.common.DateTime
import slatekit.common.Status
import slatekit.common.ext.isFilteredOut
import slatekit.common.ext.isInBadRequestRange
import slatekit.common.ext.isInFailureRange
import slatekit.common.ext.isInSuccessRange
import slatekit.common.metrics.Metrics


/**
 * Stores the state of the function execution
 */
interface FunctionState<out T> where T:FunctionResult{
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
     * Metrics to capture counts of attempts, runs, failures, etc
     */
    val metrics:Metrics

    /**
     * The last result of running the function
     */
    val lastResult: T?

    /**
     * Increments the metrics
     */
    fun increment(code:Int, tags:List<String>?){
        metrics.count("${info.nameId}_total_attempt", tags)
        when {
            code.isInSuccessRange()    -> metrics.count("${info.nameId}_total_success", tags)
            code.isFilteredOut()       -> metrics.count("${info.nameId}_total_ignored", tags)
            code.isInBadRequestRange() -> metrics.count("${info.nameId}_total_invalid", tags)
            code.isInFailureRange()    -> metrics.count("${info.nameId}_total_failure", tags)
            else                       -> metrics.count("${info.nameId}_total_unknown", tags)
        }
    }

    fun countAttempt():Long = metricCount("${info.nameId}_total_attempt")
    fun countSuccess():Long = metricCount("${info.nameId}_total_success")
    fun countIgnored():Long = metricCount("${info.nameId}_total_ignored")
    fun countInvalid():Long = metricCount("${info.nameId}_total_invalid")
    fun countFailure():Long = metricCount("${info.nameId}_total_failure")
    fun countUnknown():Long = metricCount("${info.nameId}_total_unknown")

    fun metricCount(name:String):Long {
        return metrics.total (name).toLong()
    }
}
package slatekit.core.gate

/**
 * @requestsBeforeTracking   : number of requests to process at startup before checking for errors
 *                             this is just to provide some warmup and have enough requests for proper
 *                             determination of error percentages. e.g. if 1 error out of 2 requests,
 *                             thats already 50%, but it could be 1 error out of 20 if let it run
 * @errorThresholdPrecentage : The error percentage that triggers closing the gate
 * @volumeThresholdPerMinute : The volume threshold of total requests that trigger
 * @retryCount               : The number of tries to retry a single failure
 * @subCountResetLimit       : The limit until the subcount is reset
 * @reOpenTimesInMinutes     : The time in minutes to re-open the gate after each close.
 *                             This is an exponential back-off
 */
data class GateSettings(
        val requestsBeforeTracking  : Int = 21,
        val errorThresholdPrecentage: Double = .05,
        val volumeThresholdPerMinute: Long = 0,
        val retryCount              : Int = 0,
        val subCountResetLimit      : Long = 1000000,
        val reOpenTimesInSeconds     :List<Int> = listOf(120, 300, 600, 1800, 3600, 7200, 14000, 28000)
)
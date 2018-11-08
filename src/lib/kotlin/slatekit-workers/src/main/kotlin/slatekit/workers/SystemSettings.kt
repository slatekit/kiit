package slatekit.workers

/**
 * Settings for each worker
 */
data class SystemSettings(
    val enableAutoStart: Boolean = true,
    val pauseBetweenCycles: Boolean = false,
    val pauseTimeInSeconds: Int = 2,
    val exponentialWaitTimes: List<Int> = listOf(2, 5, 10, 20, 30, 60, 120, 240)
)

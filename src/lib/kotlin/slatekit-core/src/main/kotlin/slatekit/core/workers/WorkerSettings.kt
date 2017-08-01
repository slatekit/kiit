package slatekit.core.workers

/**
 * Settings for each worker
 */
data class WorkerSettings(
        val errorLimit         : Int     = 10,
        val enableRestart      : Boolean = true,
        val batchSize          : Int     = 10,
        val isOngoing          : Boolean = false,
        val waitTimeInSeconds  : Int     = 5,
        val pauseTimeInSeconds : Int     = 5,
        val stopTimeInSeconds  : Int     = 30
)

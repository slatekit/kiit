package slatekit.jobs

/**
 * Settings for each worker
 * @param errorLimit: The percentage of errors allows before this worker is paused
 *
 */
interface JobSettings {
    val errorLimit: Double
    val enableRestart: Boolean
    val batchSize: Int
    val isOngoing: Boolean
    val waitTimeInSeconds: Int
    val pauseTimeInSeconds: Int
    val stopTimeInSeconds: Int
}

data class WorkerSettings(
    override val errorLimit: Double = .2,
    override val enableRestart: Boolean = true,
    override val batchSize: Int = 10,
    override val isOngoing: Boolean = false,
    override val waitTimeInSeconds: Int = 5,
    override val pauseTimeInSeconds: Int = 5,
    override val stopTimeInSeconds: Int = 30
) : JobSettings

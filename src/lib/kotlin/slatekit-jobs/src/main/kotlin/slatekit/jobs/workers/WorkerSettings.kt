package slatekit.jobs.workers

/**
 * Settings for each worker
 * @param errorLimit: The percentage of errors allows before this worker is paused
 *
 */
interface JobSettings {
    val batchSize: Int
    val pauseTimeInSeconds: Int
}

data class WorkerSettings(
    override val batchSize: Int = 10,
    override val pauseTimeInSeconds: Int = 5
) : JobSettings

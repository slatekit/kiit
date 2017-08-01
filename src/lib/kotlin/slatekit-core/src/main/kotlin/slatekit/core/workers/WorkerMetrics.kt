package slatekit.core.workers

/**
 * Metrics for each worker.
 * This captures info about how many times a worker has run, the last run time,
 * error count and more.
 */
data class WorkerMetrics(val name:String)
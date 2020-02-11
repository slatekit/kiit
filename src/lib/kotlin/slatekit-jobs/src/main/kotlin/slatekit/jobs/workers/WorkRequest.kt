package slatekit.jobs.workers

import slatekit.jobs.Task

/**
 * Represents a request for work by a worker
 * @param context: The current context of the worker including its job, worker, stats, etc
 * @param task : The current task for the worker to work on.
 */
data class WorkRequest(val context: WorkerContext, val task: Task)

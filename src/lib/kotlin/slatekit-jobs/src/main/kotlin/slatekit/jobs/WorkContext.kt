package slatekit.jobs

data class WorkContext(val worker: Workable<*>, val stats: WorkerStats)
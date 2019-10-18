package slatekit.jobs


data class WorkRequest(val job: Job, val worker: Worker<*>, val task: Task)
package slatekit.workers

data class WorkRequest(val job:Job, val queue: Queue, val worker:Worker<*>)
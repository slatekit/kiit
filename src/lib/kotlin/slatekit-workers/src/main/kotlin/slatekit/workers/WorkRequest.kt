package slatekit.workers

import slatekit.workers.core.QueueInfo

data class WorkRequest(val job:Job, val queue: QueueInfo, val worker:Worker<*>)
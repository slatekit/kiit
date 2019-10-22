package slatekit.jobs

import slatekit.common.Identity
import slatekit.common.metrics.Recorder

data class WorkerContext(val id: Identity, val worker: Worker<*>, val stats:Recorder<Task, WorkState>, val task:Task)
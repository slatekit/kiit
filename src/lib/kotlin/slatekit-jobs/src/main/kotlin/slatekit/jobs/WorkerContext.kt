package slatekit.jobs

import slatekit.common.Identity
import slatekit.common.metrics.Recorder

data class WorkerContext(val jobId: Identity, val worker: Worker<*>, val stats:Recorder<Task, WorkResult>)
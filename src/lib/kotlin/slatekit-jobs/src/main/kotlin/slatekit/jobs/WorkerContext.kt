package slatekit.jobs

import slatekit.common.metrics.Calls

data class WorkerContext(val worker: Worker<*>, val runs: Calls)
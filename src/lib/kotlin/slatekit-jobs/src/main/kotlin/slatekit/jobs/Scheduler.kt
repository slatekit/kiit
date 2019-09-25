package slatekit.jobs

import slatekit.common.DateTime

interface Scheduler {

    suspend fun schedule(time:DateTime, op: suspend () -> Unit)
}
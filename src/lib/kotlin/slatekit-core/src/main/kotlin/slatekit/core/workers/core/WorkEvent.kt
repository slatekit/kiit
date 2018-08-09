package slatekit.core.workers.core

import slatekit.core.workers.Group
import slatekit.core.workers.Worker

data class WorkEvent(val group: Group?, val worker: Worker<*>, val state:String)
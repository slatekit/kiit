package slatekit.core.slatekit.core.scheduler

import slatekit.common.DateTime
import slatekit.core.scheduler.Task

data class TaskRequest(val task:Task, val timestamp: DateTime)
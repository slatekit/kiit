package slatekit.functions.scheduler

import slatekit.common.DateTime
import slatekit.functions.scheduler.Task

data class TaskRequest(val task:Task, val timestamp: DateTime)
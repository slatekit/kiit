package slatekit.workers.core

import slatekit.workers.Job
import slatekit.workers.Worker

/**
 * A general purpose class to represent an event in the system, such as
 * but not limited to state changes ( started, stopped ), or other notifications
 */
data class Event(val type: EventType,
                 val sender: Any,
                 val worker: Worker<*>,
                 val state: String,
                 val job: Job?,
                 val queue: String?)

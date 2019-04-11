package slatekit.entities.queues

import slatekit.common.queues.QueueEntryStatus

interface EntityQueable {
    val status: QueueEntryStatus
    val attributes:String

    fun withAttributes(attributes: Map<String, Any>):EntityQueable
}
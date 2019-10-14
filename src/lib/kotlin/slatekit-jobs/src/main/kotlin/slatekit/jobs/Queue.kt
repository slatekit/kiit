package slatekit.jobs

import slatekit.common.queues.QueueSource
import slatekit.common.queues.QueueSourceInMemory
import slatekit.common.queues.QueueStringConverter

/**
 * Wraps the underlying queue holding the messages with other metadata ( name, priority )
 * The metadata can be extended in the future
 *
 * @param name     : Name of the queue ( e.g. "notifications" )
 * @param priority : Priority of the queue ( low, medium, high ) for weighted selection
 * @param queue    : The actual queue source / implementation
 */
data class Queue(val name: String, val priority: Priority, val queue: QueueSource<String>) {

    companion object {
        val source = QueueSourceInMemory<String>("", QueueStringConverter())
        val empty = Queue("empty", Priority.Low, source)
    }
}

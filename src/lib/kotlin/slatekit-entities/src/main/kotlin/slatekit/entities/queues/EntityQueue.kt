package slatekit.entities.queues

import slatekit.common.queues.QueueEntry
import slatekit.common.queues.QueueEntryStatus
import slatekit.common.queues.QueueSource
import slatekit.common.queues.QueueValueConverter
import slatekit.entities.Entity
import slatekit.entities.EntityRepo
import slatekit.query.Query
import slatekit.query.where
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try

/**
 * An adaptor that exposes the Entity Repository as a Queue.
 *
 * NOTE:
 * This should be used in a single-threaded context and is designed for use in mobile devices
 * rather than the server. If using on the server, ensure only 1 thread is accessing this queue.
 *
 * REASON:
 * This is because if multiple clients are using this queue and calling next(), next(batch),
 * after fetching the items, this component changes the status to "in processing".
 * During this time another item can get the same next item from the queue and can lead to
 * inconsistent behaviour.
 *
 */
class EntityQueue<T>(queueName: String, val repo:EntityRepo<Long, T>) : QueueSource<T> where T: EntityQueable, T:Entity<Long> {


    override val name: String = queueName
    override val converter: QueueValueConverter<T> = EntityConverter()


    /**
     * Total items int the queue
     */
    override fun count(): Int {
        return repo.count().toInt()
    }


    /**
     * Gets the next available item from the queue
     */
    override fun next(): QueueEntry<T>? {
        val item = repo.findFirst(Query().where(EntityQueable::status, "=", QueueEntryStatus.InActive.value))
        return item?.let { toEntry(it) }
    }


    /**
     * Gets the next available N items from the queue that are inactive ( not being processed )
     */
    override fun next(size: Int): List<QueueEntry<T>>? {
        val items = repo.find(Query()
                .where(EntityQueable::status, "=", QueueEntryStatus.InActive.value)
                .limit(size)
        )
        return items.map { toEntry(it) }
    }


    /**
     * Sends the entity to the queue
     */
    override fun send(value: T, tagName: String, tagValue: String): Try<String> {
        return send(value, mapOf(tagName to tagValue))
    }


    /**
     * Sends the entity to the queue using the attributes supplied
     */
    override fun send(value: T, attributes: Map<String, Any>): Try<String> {
        return create(value)
    }


    /**
     * Sends the entity from the file into the queue
     */
    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String> {
        return Success("")
    }


    /**
     * Creates the entity in the queue
     */
    private fun create(item: T): Try<String> {
        val result = repo.create(item)
        return if (result > 0) {
            Success(result.toString())
        } else {
            Failure(Exception("Error storing error message"))
        }
    }


    /**
     * Converts the entity into a queue entry
     */
    private fun toEntry(item: T): QueueEntry<T> {
        return EntityQueueEntry(item)
    }
}
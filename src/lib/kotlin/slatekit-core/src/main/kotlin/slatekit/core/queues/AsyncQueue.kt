package slatekit.core.queues

import slatekit.results.Try

interface AsyncQueue<T> {
    val name: String
    val converter: QueueValueConverter<T>
    suspend fun init()

    suspend fun close()

    suspend fun count(): Int

    suspend fun next(): QueueEntry<T>?

    suspend fun next(size: Int = 10): List<QueueEntry<T>>?

    suspend fun done(entry: QueueEntry<T>?): Try<QueueEntry<T>>

    suspend fun done(entries: List<QueueEntry<T>>?):slatekit.results.Result<String, List<Pair<QueueEntry<T>, Exception>>> {
        return completeAll(entries) { done(it) }
    }

    suspend fun abandon(entry: QueueEntry<T>?): Try<QueueEntry<T>>

    suspend fun send(value: T): Try<String> = send(value, null)

    suspend fun send(value: T, tagName: String, tagValue: String): Try<String>

    suspend fun send(value: T, attributes: Map<String, Any>?): Try<String>

    suspend fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String>


    companion object {

        fun <T> of(queue: Queue<T>): AsyncQueue<T> {
            // Queues
            val queue = WrappedAsyncQueue<T>(queue)
            return queue
        }
    }
}


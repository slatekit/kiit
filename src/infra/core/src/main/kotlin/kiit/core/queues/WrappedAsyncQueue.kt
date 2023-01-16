package kiit.core.queues

import kiit.results.Try

class WrappedAsyncQueue<T>(val queue:Queue<T>) : AsyncQueue<T> {

    override val name: String = queue.name
    override val converter: QueueValueConverter<T> = queue.converter

    override suspend fun init() {
        queue.init()
    }

    override suspend fun close() {
        queue.close()
    }

    override suspend fun count(): Int {
        return queue.count()
    }

    override suspend fun next(): QueueEntry<T>? {
        return queue.next()
    }

    override suspend fun next(size: Int): List<QueueEntry<T>>? {
        return queue.next(size)
    }

    override suspend fun done(entry: QueueEntry<T>?):Try<QueueEntry<T>> {
        return queue.done(entry)
    }

    override suspend fun done(entries: List<QueueEntry<T>>?):kiit.results.Result<String, List<Pair<QueueEntry<T>, Throwable>>> {
        return queue.done(entries)
    }

    override suspend fun abandon(entry: QueueEntry<T>?): Try<QueueEntry<T>> {
        return queue.abandon(entry)
    }

    override suspend fun send(value: T, tagName: String, tagValue: String): Try<String> {
        return queue.send(value, tagName, tagValue)
    }

    override suspend fun send(value: T, attributes: Map<String, Any>?): Try<String> {
        return queue.send(value, attributes)
    }

    override suspend fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String> {
        return queue.sendFromFile(fileNameLocal, tagName, tagValue)
    }
}
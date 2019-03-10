package slatekit.integration.errors

import slatekit.common.DateTime
import slatekit.common.Random
import slatekit.common.queues.QueueEntry
import slatekit.common.queues.QueueSource
import slatekit.common.queues.QueueValueConverter
import slatekit.query.Query
import slatekit.integration.common.AppEntContext
import slatekit.meta.Serialization
import slatekit.query.where
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try

class ErrorItemQueue(queueName: String = "errors", appEntContext: AppEntContext) : QueueSource<ErrorItem> {

    val svc = appEntContext.ent.getSvc<Long, ErrorItem>(ErrorItem::class)

    override val name: String = queueName
    override val converter: QueueValueConverter<ErrorItem> = ErrorItemConverter()

    override fun count(): Int {
        return svc.count().toInt()
    }


    override fun next(): QueueEntry<ErrorItem>? {
        val item = svc.findFirst(Query().where(ErrorItem::status, "=", ErrorItemStatus.Active.value))
        return item?.let { toEntry(it) }
    }


    override fun next(size: Int): List<QueueEntry<ErrorItem>>? {
        val items = svc.find(Query()
                .where(ErrorItem::status, "=", ErrorItemStatus.Active.value)
                .limit(size)
        )
        return items.map { toEntry(it) }
    }


    override fun send(value: ErrorItem, tagName: String, tagValue: String): Try<String> {
        return send(value, mapOf(tagName to tagValue))
    }


    override fun send(value: ErrorItem, attributes: Map<String, Any>): Try<String> {
        return create(value)
    }


    override fun sendFromFile(fileNameLocal: String, tagName: String, tagValue: String): Try<String> {
        return Success("")
    }


    private fun create(item: ErrorItem): Try<String> {
        val result = svc.create(item)
        return if (result > 0) {
            Success(result.toString())
        } else {
            Failure(Exception("Error storing error message"))
        }
    }


    private fun toEntry(item: ErrorItem): QueueEntry<ErrorItem> {
        return ErrorQueueEntry(item)
    }


    class ErrorItemConverter: QueueValueConverter<ErrorItem> {
        override fun convertToString(item: ErrorItem?): String? {
            return item?.let { Serialization.json().serialize(item) }
        }

        override fun convertFromString(content: String?): ErrorItem? {
            TODO("Switch to Jackson serializer and/or fix the deserializer in slatekit.meta")
        }

    }


    data class ErrorQueueEntry<ErrorItem>(
            val entry: ErrorItem,
            val tags: Map<String, Any>? = null,
            override val id: String = Random.uuid(),
            override val createdAt: DateTime = DateTime.now()
    ) : QueueEntry<ErrorItem> {

        /**
         * This is the value itself for the default implementation
         */
        override val raw: Any? = entry


        override fun getValue(): ErrorItem? {
            return entry
        }


        override fun getTag(name: String): String? {
            return when (tags) {
                null -> null
                else -> if (tags.containsKey(name)) tags[name]?.toString() else null
            }
        }
    }
}
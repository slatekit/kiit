package slatekit.entities.queues

import slatekit.common.queues.QueueValueConverter
import slatekit.meta.Serialization


class EntityConverter<T>: QueueValueConverter<T> {
    override fun convertToString(item: T?): String? {
        return item?.let { Serialization.json().serialize(item) }
    }

    override fun convertFromString(content: String?): T? {
        TODO("Switch to Jackson serializer and/or fix the deserializer in slatekit.meta")
    }
}
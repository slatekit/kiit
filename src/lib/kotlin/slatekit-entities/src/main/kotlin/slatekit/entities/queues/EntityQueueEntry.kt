package slatekit.entities.queues

import slatekit.common.DateTime
import slatekit.common.Random
import slatekit.common.queues.QueueEntry
import slatekit.entities.Entity

data class EntityQueueEntry<T>(
        val entry: T,
        val tags: Map<String, Any>? = null,
        override val id: String = Random.uuid(),
        override val createdAt: DateTime = DateTime.now()
) : QueueEntry<T> where T: Entity<Long> {

    override val raw: Any? = entry


    override fun getValue(): T? {
        return entry
    }


    override fun getTag(name: String): String? {
        return when (tags) {
            null -> null
            else -> if (tags.containsKey(name)) tags[name]?.toString() else null
        }
    }
}
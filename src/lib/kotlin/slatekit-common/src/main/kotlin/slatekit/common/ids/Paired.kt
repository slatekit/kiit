package slatekit.common.ids

import java.util.*
import java.util.concurrent.atomic.AtomicLong


open class Paired {
    protected val _serialId = AtomicLong(0L)

    open fun next(): Pair<Long, UUID> = Pair(nextId(), nextUUID())
    open fun nextId(): Long = _serialId.incrementAndGet()
    open fun nextUUID(): UUID = UUID.randomUUID()
}

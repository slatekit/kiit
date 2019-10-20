package slatekit.jobs.support

import java.util.*
import java.util.concurrent.atomic.AtomicLong

open class JobId {
    protected val _serialId = AtomicLong(0L)

    open fun nextId():Long = _serialId.incrementAndGet()
    open fun nextUUID(): UUID = UUID.randomUUID()
}
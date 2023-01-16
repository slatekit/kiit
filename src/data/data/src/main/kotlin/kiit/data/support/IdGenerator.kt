package kiit.data.support

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

interface IdGenerator<TId : Comparable<TId>> {
    fun nextId(): TId
}


class LongIdGenerator : IdGenerator<Long> {
    private val id = AtomicLong(0L)
    override fun nextId(): Long = id.incrementAndGet()
}


class IntIdGenerator : IdGenerator<Int> {
    private val id = AtomicInteger(0)
    override fun nextId(): Int = id.incrementAndGet()
}

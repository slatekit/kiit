package slatekit.tracking

import java.util.concurrent.atomic.AtomicLong

/**
 * Simple counter for a value
 */
data class Counter(override val tags: List<Tag> = listOf(), val customTags:List<String>? = null) : Tagged, Count {

    private val value = AtomicLong(0L)

    override fun inc(): Long = value.incrementAndGet()
    override fun dec(): Long = value.decrementAndGet()
    override fun get(): Long = value.get()
    override fun set(newValue:Long) = value.set(newValue)
}



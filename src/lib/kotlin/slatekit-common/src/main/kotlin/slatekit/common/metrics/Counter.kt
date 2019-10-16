package slatekit.common.metrics

import java.util.concurrent.atomic.AtomicLong

/**
 * Simple counter for a value
 */
data class Counter(override val tags: List<Tag>, val customTags:List<String>? = null) : Tagged {

    private val value = AtomicLong(0L)

    fun inc(): Long = value.incrementAndGet()
    fun dec(): Long = value.decrementAndGet()
    fun get(): Long = value.get()
    fun set(newValue:Long) = value.set(newValue)
}
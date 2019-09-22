package slatekit.common.metrics

import slatekit.common.ids.Identity
import java.util.concurrent.atomic.AtomicLong

class Counters(val id: Identity, val custom:List<String>? = null) {

    private val uniqueId = "${id.name}-${id.uuid}"
    private val processCounter = AtomicLong(0L)
    private val successCounter = AtomicLong(0L)
    private val deniedCounter = AtomicLong(0L)
    private val invalidCounter = AtomicLong(0L)
    private val ignoredCounter = AtomicLong(0L)
    private val erroredCounter = AtomicLong(0L)
    private val unexpectedCounter = AtomicLong(0L)
    private val customCounters = custom?.let { c -> c.map{ it to AtomicLong(0L) }.toMap() } ?: mapOf()


    /**
     * Reset all values back to 0
     */
    fun reset() {
        processCounter.set(0L)
        successCounter.set(0L)
        invalidCounter.set(0L)
        ignoredCounter.set(0L)
        deniedCounter.set(0L)
        erroredCounter.set(0L)
        unexpectedCounter.set(0L)
    }


    /**
     * Increment the counters for the different states
     * @return
     */
    fun processed():Long  = processCounter.incrementAndGet()
    fun succeeded():Long  = successCounter.incrementAndGet()
    fun denied():Long     = deniedCounter.incrementAndGet()
    fun invalid():Long    = invalidCounter.incrementAndGet()
    fun ignored():Long    = ignoredCounter.incrementAndGet()
    fun errored():Long    = erroredCounter.incrementAndGet()
    fun unexpected():Long = unexpectedCounter.incrementAndGet()
    fun custom(name:String) = getCustom(name)?.let{ c -> c.incrementAndGet() }


    fun totalProcessed ():Long = processCounter.get()
    fun totalSucceeded ():Long = successCounter.get()
    fun totalInvalid   ():Long = invalidCounter.get()
    fun totalIgnored   ():Long = ignoredCounter.get()
    fun totalDenied    ():Long = deniedCounter.get()
    fun totalErrored   ():Long = erroredCounter.get()
    fun totalUnexpected():Long = unexpectedCounter.get()
    fun totalCustom(name:String):Long = getCustom(name)?.let { c -> c.get() } ?:0L


    private fun getCustom(name:String):AtomicLong? {
        return if(customCounters.contains(name)) customCounters[name] else null
    }


    private fun inc(counter:AtomicLong, value:Int): Long {
        val current = counter.get()
        val updated = current + value
        counter.set(updated)
        return updated
    }
}